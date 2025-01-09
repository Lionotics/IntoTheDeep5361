package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Canvas;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

@Config
public class BrickAngleDetector implements VisionProcessor {
    private double angle;
    public boolean isBlue;
    public Telemetry telemetry;
    public static Scalar hsvLowerYellow = new Scalar(20,100,100),
            hsvUpperYellow = new Scalar(30,255,255);
    public static Scalar hsvLowerBlue = new Scalar(110,50,50),
           hsvUpperBlue = new Scalar(130,255,255);
    public static Scalar hsvLowerRed = new Scalar(90,100,100),
            hsvUpperRed = new Scalar(180,255,255);
    public Scalar hsvLowerTeam, hsvUpperTeam;
    public BrickAngleDetector(boolean isBlue, Telemetry telemetry) {
        this.isBlue = isBlue;
        hsvLowerTeam = isBlue ? hsvLowerBlue : hsvLowerRed;
        hsvUpperTeam = isBlue ? hsvUpperBlue : hsvUpperRed;
        this.telemetry = telemetry;
    }

    @Deprecated
    private Mat generateMask(Mat frame) {
        Mat maskYellow = new Mat(), maskTeam = new Mat(), returnee = new Mat();
        Core.inRange(frame,hsvLowerYellow,hsvUpperYellow,maskYellow);
        Core.inRange(frame,hsvLowerTeam,hsvUpperTeam,maskTeam);
        Core.bitwise_or(maskYellow,maskTeam,returnee);

        //Memory management for the cool kids
        maskYellow.release();
        maskTeam.release();

        return returnee;
    }

    @Deprecated
    public List<MatOfPoint> findContours(Mat frame) {
        Mat gray = new Mat();
        Imgproc.cvtColor(frame, gray, Imgproc.COLOR_RGB2GRAY);

        // Apply Gaussian Blurring to enhance the contours
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(15,15),0);

        // Find edges
        Mat edges = new Mat();
        Imgproc.Canny(blurred, edges, 50, 150);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(edges, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Memory management for the cool kids
        gray.release();
        blurred.release();
        edges.release();

        return contours;
    }

    // Returns in format y=mx+b
    @Deprecated
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

        return new double[] {m,b};
    }


    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }

    // Moreinu Ve'Rabeinu Ma'ara De'Asra ChatGPT found this answer online, and it is cracked
    // Don't question its methods
    @Override
    public Object processFrame(Mat frame, long useless) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(frame, hsv, Imgproc.COLOR_RGB2HSV);

        // Define yellow color range in HSV

        // Create a mask for yellow color
        Mat yelMask = new Mat(), teamMask = new Mat(), mask = new Mat();
        Core.inRange(hsv, hsvLowerYellow, hsvUpperYellow, yelMask);
        Core.inRange(hsv, hsvLowerTeam, hsvUpperTeam, teamMask);
        Core.bitwise_or(yelMask,teamMask,mask);

        // Perform morphological operations to remove noise
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(5, 5));
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel);
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Imgproc.findContours(mask, contours, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        double angle = Double.NaN; // Default angle if no valid contour is found

        for (MatOfPoint contour : contours) {
            // Approximate the contour to a polygon
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());

            // Fit a bounding rectangle to the contour
            RotatedRect rotatedRect = Imgproc.minAreaRect(contour2f);

            // Reject rectangles that are too small
            if (rotatedRect.size.height * rotatedRect.size.width < 50) {
                Imgproc.putText(frame, "Rect not found", new Point(50,50), Imgproc.FONT_HERSHEY_SCRIPT_COMPLEX, 1.0, new Scalar(0,0,255), 2);
                return null;
            }

            // Assume rotatedRect is a valid RotatedRect object and frame is the Mat on which you want to draw
            Point[] vertices = new Point[4];
            rotatedRect.points(vertices);

            // Draw lines between the vertices
            for (int i = 0; i < 4; i++) {
                Imgproc.line(frame, vertices[i], vertices[(i + 1) % 4], new Scalar(0, 255, 0), 2);
            }

            // Calculate the angle of rotation
            angle = rotatedRect.angle;

            // Adjust angle for consistent orientation
            if (rotatedRect.size.width < rotatedRect.size.height) {
                angle += 90;
            }

            contour2f.release();
        }

        // Release resources
        hsv.release();
        mask.release();
        kernel.release();


        Imgproc.putText(frame, "Angle: " + angle, new Point(50,50), Imgproc.FONT_HERSHEY_SCRIPT_COMPLEX, 1.0, new Scalar(0,0,255), 2);
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
