import org.opencv.core.*;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.videoio.VideoCapture;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;

public class FaceRecognition {

    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME); // Load native OpenCV library
    }

    public static boolean recognizeFace(String empId) {
        CascadeClassifier faceDetector = new CascadeClassifier("haarcascade_frontalface_default.xml");
        VideoCapture camera = new VideoCapture(0);

        if (!camera.isOpened()) {
            System.out.println("Cannot open camera");
            return false;
        }

        Mat frame = new Mat();
        System.out.println("Please face the camera...");

        long start = System.currentTimeMillis();
        boolean success = false;

        while ((System.currentTimeMillis() - start) < 10000) {
            if (camera.read(frame)) {
                MatOfRect faceDetections = new MatOfRect();
                faceDetector.detectMultiScale(frame, faceDetections);

                for (Rect rect : faceDetections.toArray()) {
                    Imgproc.rectangle(frame, rect, new Scalar(0, 255, 0));
                    Mat face = new Mat(frame, rect);
                    Imgcodecs.imwrite("live.jpg", face); // Save current face
                    success = true;
                    break;
                }

                if (success) break;
            }
        }

        camera.release();

        if (!success) {
            System.out.println("No face detected!");
            return false;
        }

        // Now match saved face (live.jpg) with employee DB
        // Dummy logic (replace with LBPHRecognizer train/load)
        File storedFace = new File("faces/" + empId + ".jpg");
        if (storedFace.exists()) {
            System.out.println("Face matched for employee " + empId);
            return true;
        } else {
            System.out.println("No registered face for ID " + empId);
            return false;
        }
    }
}
