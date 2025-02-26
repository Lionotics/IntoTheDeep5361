package teamcode.vision;

import android.graphics.Canvas;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.KeyPoint;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.SimpleBlobDetector;
import org.opencv.features2d.SimpleBlobDetector_Params;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Config
public class BrickAngleDetector implements VisionProcessor {
    public static Scalar hsvLowerYellow = new Scalar(10, 140, 180),
            hsvUpperYellow = new Scalar(40, 220, 255);
    public static Scalar hsvLowerBlue = new Scalar(110, 130, 100),
            hsvUpperBlue = new Scalar(125, 255, 255);
    public static Scalar hsvLowerRed = new Scalar(0, 100, 100),
            hsvUpperRed = new Scalar(10, 255, 255);
    public Telemetry telemetry;
    public Scalar hsvLowerTeam, hsvUpperTeam;
    private double angle;
    public static class AngleData {
        public enum DataType {
            RECT_DETECT, BLOB_DETECT, THRESH_DETECT
        }
        public double angle;
        public double confidence;
        public DataType dataType;
        public static List<AngleData> database = new ArrayList<>();
        public static int getTotalData() {
            return database.size();
        }
        public static int getTotalDataType(DataType targetData) {
            return (int) database.stream()
                    .filter(data -> data.dataType == targetData)
                    .count();
        }

        private static double weightedAverage() {
            double sum = 0, weight = 0;
            for (AngleData d : database) {
                sum += d.angle * d.confidence;
                weight += d.confidence;
            }
            return sum / weight;
        }

        public AngleData(double angle, double confidence, DataType dataType) {
            this.angle = angle;
            this.confidence = confidence;
            this.dataType = dataType;
        }
    }

    public BrickAngleDetector(boolean isBlue, Telemetry telemetry) {
        hsvLowerTeam = isBlue ? hsvLowerBlue : hsvLowerRed;
        hsvUpperTeam = isBlue ? hsvUpperBlue : hsvUpperRed;
        this.telemetry = telemetry;
    }

    private Mat generateMask(Mat frame) {
        Mat hsv = new Mat();
        if (frame == null) {return new Mat();}
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        // Create a mask for yellow color
        Mat yelMask = new Mat(), teamMask = new Mat(), mask = new Mat();
        Core.inRange(hsv, hsvLowerYellow, hsvUpperYellow, yelMask);
        Core.inRange(hsv, hsvLowerTeam, hsvUpperTeam, teamMask);
        Core.bitwise_or(yelMask, teamMask, mask);

        // Memory management for the cool kids
        hsv.release();
        yelMask.release();
        teamMask.release();

        return mask;
    }

    // Returns in format y=mx+b
    private double[] bestFitLineSlope(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;

        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double m = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double b = (sumY - m * sumX) / n;

        return new double[]{m, b};
    }

    // If this works, this built-in class does wonders, but it tends to not detect stuff
    // properly, so we can resolve it another way
    private double openCVRectangleDetection(Mat mask) {
        if (mask == null) {return Double.NaN;}
        // Perform morphological operations to remove noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, new Mat(),
                Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double angle = Double.NaN; // Default angle if no valid contour is found

        for (MatOfPoint contour : contours) {
            // Approximate the contour to a polygon
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

            // Fit a bounding rectangle to the contour
            RotatedRect rotatedRect = Imgproc.minAreaRect(contour2f);

            // Calculate the angle of rotation
            angle = rotatedRect.angle;

            // Adjust angle for consistent orientation
            if (rotatedRect.size.width < rotatedRect.size.height) {
                angle += 90;
            }

            contour2f.release();
        }

        // Release resources
        mask.release();
        kernel.release();

        return angle;
    }

    // Instead of picking out rectangles, we try to clean up extraneous data from the mask
    private double blobDetection(Mat mask) {
        if (mask == null) {return Double.NaN;}
        // Look for Continuous Blobs with a big size (remember block is close to the camera)
        SimpleBlobDetector_Params params = new SimpleBlobDetector_Params();
        params.set_filterByArea(true);
        params.set_minArea(1000);
        params.set_maxArea(100000);
        params.set_filterByCircularity(false);
        params.set_filterByConvexity(false);
        params.set_filterByInertia(false);

        // Create a blob detector with the parameters
        SimpleBlobDetector detector = SimpleBlobDetector.create(params);

        // Detect blobs in the mask
        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        detector.detect(mask, keypoints);

        // If we have no blobs, we can't do anything
        if (keypoints.toList().isEmpty()) {
            return Double.NaN;
        }

        // Else, filter out all data that isn't a blob
        // Create a blank Mat for output
        Mat dartBoard = Mat.zeros(mask.size(), mask.type());

        // Iterate over keypoints and preserve only those regions
        for (KeyPoint keypoint : keypoints.toArray()) {
            Point center = keypoint.pt;
            int radius = (int) (keypoint.size / 2);

            // Copy circular region around the keypoint
            Imgproc.circle(dartBoard, center, radius, new Scalar(255, 255, 255), -1);
        }

        // Use the mask to calculate the angle
        return maskToAngle(dartBoard);
    }

    private double maskToAngle(Mat mask) {
        // Fetch all positive pixels in the dartboard
        ArrayList<Integer> nonZeroPoints_x = new ArrayList<>();
        ArrayList<Integer> nonZeroPoints_y = new ArrayList<>();
        for (int row = 0; row < mask.rows(); row++) {
            for (int col = 0; col < mask.cols(); col++) {
                if (mask.get(row, col)[0] > 0) {
                    nonZeroPoints_x.add(col);
                    nonZeroPoints_y.add(row);
                }
            }
        }

        // Getting the m of the best fit line after giving it all the points
        double slope = bestFitLineSlope(
                // Now this looks complicated, but the gist of it that we are converting
                // the ArrayList to a primitive double[] array
                nonZeroPoints_x.stream().mapToDouble(i -> i).toArray(),
                nonZeroPoints_y.stream().mapToDouble(i -> i).toArray()
        )[0];

        // Calculate the angle of the line
        return angle = Math.toDegrees(Math.atan(slope));
    }

    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }

    @Override
    public Object processFrame(Mat frame, long useless) {
        // Before we start: NEVER TRY TO SAVE ANYTHING TO THE FRAME
        // It screws up the pipeline and causes a lot of issues

        if (frame == null) {return null;}

        // Generate a mask for the frame. Plan A, B, and C all need it.
        Mat mask = generateMask(frame);

        if (mask.empty()) {return null;}

        // This is our Plan A - use a OpenCV built-in rectangle detection
        //.If it works, we're done, ship it and send it back
        double planA = openCVRectangleDetection(mask);

        // Make sure we get some valid data before returning
        if (!Double.isNaN(planA) && planA != 0.0d && planA != 90 && planA != 180) {
            angle = planA;
            AngleData.database.add(
                    new AngleData(angle, 1,AngleData.DataType.RECT_DETECT)
            );
            Imgproc.putText(frame, "Angle: " + angle,
                    new Point(50, 50),
                    Imgproc.FONT_HERSHEY_PLAIN,
                    1.0,
                    new Scalar(0, 0, 255),
                    2
            );
            return null;
        }

        // If we're here, Plan A failed, so we need to go to Plan B
        // Attempt to remove extraneous data (Reduce noise (nonsense data) in the mask)
        //TODO: Figure out why this throws this:
        // OpenCV(4.10.0) Error: Assertion failed (!_src.empty()) in GaussianBlur
        //Imgproc.GaussianBlur(mask, mask, new Size(5, 5), 0);

        // Plan B - Find blobs in the mask, and see if there are any hits
        double planB = blobDetection(mask);
        if (!Double.isNaN(planB)) {
            angle = planB;
            AngleData.database.add(
                    new AngleData(angle, .1, AngleData.DataType.BLOB_DETECT)
            );

            // If we have a lot of data, we can try to use the approximation instead
            if (AngleData.getTotalData() > 100) {
                angle = AngleData.weightedAverage();
            }
            Imgproc.putText(frame, "Angle: " + angle,
                    new Point(50, 50),
                    Imgproc.FONT_HERSHEY_PLAIN,
                    1.0,
                    new Scalar(0, 0, 255),
                    2
            );
            return null;
        }

        // If we're here, Plan B failed, so we need to go to Plan C
        // Now we take any data on the frame out thresh and cleanup
        double planC = angle = maskToAngle(mask);

        AngleData.database.add(
                new AngleData(angle, 0.1, AngleData.DataType.THRESH_DETECT)
        );

        // Even if we have only a little data, at this point something is better than nothing
        if (AngleData.getTotalData() > 10) {
            angle = AngleData.weightedAverage();
        }
        Imgproc.putText(frame, "Angle: " + angle,
                new Point(50, 50),
                Imgproc.FONT_HERSHEY_PLAIN,
                1.0,
                new Scalar(0, 0, 255),
                2
        );

        // Memory management for the cool kids
        mask.release();

        return null;
    }

    @Override
    public void onDrawFrame(Canvas b, int r, int u, float h, float w, Object ot) {
        // We don't need to draw anything, so this method is empty
    }

    public double getAngle() {
        return angle;
    }
}