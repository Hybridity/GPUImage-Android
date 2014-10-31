package jp.co.cyberagent.android.gpuimage;


//import org.bytedeco.javacpp.avutil;
//import org.bytedeco.javacpp.swresample;
import org.bytedeco.javacv.FFmpegFrameRecorder;
//import org.bytedeco.javacv.Frame;


import static org.bytedeco.javacpp.opencv_core.*;

public class GPUImageMovieWriter {
	private volatile FFmpegFrameRecorder recorder;
	  private boolean isPreviewOn = false;
	  	
	  long startTime = 0;
	   boolean recording = false;
	    
	    
	    private int sampleAudioRateInHz = 44100;
	    private int imageWidth = 1000;
	    private int imageHeight = 1000;
	    private int frameRate = 30;
	    
	    private String ffmpeg_link = "/mnt/sdcard/Generate/stream.flv";
	    
	    private IplImage yuvIplimage = null;
	    
	    
	public GPUImageMovieWriter (int width, int height){
    	imageWidth = width;
    	imageHeight = height;
		initRecorder();
	}
	
	public void startRecording(){
        try {
            recorder.start();
            startTime = System.currentTimeMillis();
            recording = true;
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
         recording = true;
	}
	
	public void finishRecording(){
        if (recorder != null && recording) {
            recording = false;
            try {
                recorder.stop();
                recorder.release();
            } catch (FFmpegFrameRecorder.Exception e) {
                e.printStackTrace();
            }
            recorder = null;
            recording = false;
        }
	}

	public void writeFrame(byte[] data){
		if (yuvIplimage != null && recording) {
            yuvIplimage.getByteBuffer().put(data);
        try {
            long t = 1000 * (System.currentTimeMillis() - startTime);
            if (t > recorder.getTimestamp()) {
                recorder.setTimestamp(t);
            }
            recorder.record(yuvIplimage);
        } catch (FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
		}
	}
	
	public void cancelRecording(){
		
	}
	
	public void setOutputFilePath (String s){
		ffmpeg_link = s;
	}
	
	//---------------------------------------
    // initialize ffmpeg_recorder
    //---------------------------------------
    private void initRecorder() {

    	try {
			Class.forName("org.bytedeco.javacpp.swresample");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
        if (yuvIplimage == null) {
            yuvIplimage = IplImage.create(imageWidth, imageHeight, IPL_DEPTH_8U,4);
        }
    	recorder = new FFmpegFrameRecorder(ffmpeg_link, imageWidth, imageHeight, 1);
        recorder.setFormat("flv");
        recorder.setSampleRate(sampleAudioRateInHz);
        recorder.setFrameRate(frameRate);
    }
}
