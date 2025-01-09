package org.firstinspires.ftc.teamcode.Vision;

import android.graphics.Canvas;

import com.acmerobotics.dashboard.config.Config;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.Arrays;

@Config
public class BrickAngleDetector implements VisionProcessor {
    private double angle;
    public boolean isBlue;
    public Telemetry telemetry;
    public Exception e;
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

    private Mat generateResult(Mat frame, Mat mask) {
        Mat result = new Mat();
        Core.bitwise_and(frame,frame,result,mask);
        return result;
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

        return new double[] {m,b};
    }


    @Override
    public void init(int width, int height, CameraCalibration calibration) {

    }

    @Override
    public Object processFrame(Mat frame, long useless) {
        // Separate out the colored pixels and set them aside
        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_RGB2HSV);
        Mat mask = generateMask(frame);
        Mat result = generateResult(frame, mask);

        Mat gray = new Mat();
        Imgproc.cvtColor(result, gray, Imgproc.COLOR_HSV2RGB);
        Imgproc.cvtColor(gray, gray, Imgproc.COLOR_RGB2GRAY); //Convert to grayscale for convenience

        // Find all non-black pixels
        Mat nonZero = new Mat();
        Core.findNonZero(gray, nonZero);

        // Extract the x and y coordinates of the colored pixels
        int totalPoints = nonZero.rows();
        double[] x = new double[totalPoints];
        double[] y = new double[totalPoints];

        // Get the x and y coordinates from the screen
        for (int i = 0; i < totalPoints; i++) {
            double[] point = nonZero.get(i, 0); // Each row contains (x, y)
            x[i] = point[0]; // x-coordinate
            y[i] = point[1]; // y-coordinate
        }

        double[] BestFitLineCoefficients = bestFitLineSlope(x, y); // Get the slope of the best fit line
        // NGL, ChatGPT wrote the formula for the slope, don't question it
        double m = BestFitLineCoefficients[0], b = BestFitLineCoefficients[1];

        // Calculate a line using two random points on the line

        int x1 = 50, y1 = (int) (m * x1 + b);
        int x2 = 250, y2 = (int) (m * x2 + b);

        Imgproc.cvtColor(frame, frame, Imgproc.COLOR_HSV2RGB);

        Imgproc.line(frame,new Point(x1,y1),new Point(x2,y2),new Scalar(0,255,0),2);
        //Memory management for the cool kids
        mask.release();
        result.release();
        gray.release();
        nonZero.release();

        angle = Math.toDegrees(Math.atan(m));
        // Since slope = dy/dx, and theta = arctan(dy/dx), we can find the angle from the slope
        // We convert the angle from radians to degrees and return it
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
