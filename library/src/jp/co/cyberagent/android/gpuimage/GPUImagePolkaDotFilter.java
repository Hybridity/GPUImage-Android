/*
 * Copyright (C) 2012 CyberAgent
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/* 
 * Ported from iOS to Android by Hybridity
 * 
 */

package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;
import android.util.Log;
/**
 * Applies a polkadot effect to the image.
 */
public class GPUImagePolkaDotFilter extends GPUImageFilter {
    public static final String POLKA_DOT_FRAGMENT_SHADER = "" +
    		" precision highp float;\n" +
    		
		" varying highp vec2 textureCoordinate;\n" +
		    		 
		" uniform sampler2D inputImageTexture;\n" +
		 
		" uniform highp float fractionalWidthOfPixel;\n" +
		" uniform highp float aspectRatio;\n" +
		" uniform highp float dotScaling;\n" +
	
		"void main()\n" +
			"{\n" +
		     " highp vec2 sampleDivisor = vec2(fractionalWidthOfPixel, fractionalWidthOfPixel / aspectRatio);\n" +
				     
		     " highp vec2 samplePos = textureCoordinate - mod(textureCoordinate, sampleDivisor) + 0.5 * sampleDivisor;\n" +
		     " highp vec2 textureCoordinateToUse = vec2(textureCoordinate.x, (textureCoordinate.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
		     " highp vec2 adjustedSamplePos = vec2(samplePos.x, (samplePos.y * aspectRatio + 0.5 - 0.5 * aspectRatio));\n" +
		     " highp float distanceFromSamplePoint = distance(adjustedSamplePos, textureCoordinateToUse);\n" +
		     " lowp float checkForPresenceWithinDot = step(distanceFromSamplePoint, (fractionalWidthOfPixel * 0.5) * dotScaling);\n" +
		
		     " lowp vec4 inputColor = texture2D(inputImageTexture, samplePos);\n" +
		     
		     " gl_FragColor = vec4(inputColor.rgb * checkForPresenceWithinDot, inputColor.a);\n" +
	    "}";


    private int dotScalingUniform;
    private int fractionalWidthOfAPixelUniform;
    private int aspectRatioUniform;
    private float mDot;
    private float mWidth;
    private float aspectRatio;
    
    public GPUImagePolkaDotFilter() {
        super(NO_FILTER_VERTEX_SHADER, POLKA_DOT_FRAGMENT_SHADER);
        mDot = 0.5f;
        mWidth = 0.05f;
        aspectRatio = 1.0f;
    }

    @Override
    public void onInit() {
        super.onInit();

        dotScalingUniform = GLES20.glGetUniformLocation(getProgram(), "dotScaling");
        fractionalWidthOfAPixelUniform = GLES20.glGetUniformLocation(getProgram(), "fractionalWidthOfPixel");
        aspectRatioUniform = GLES20.glGetUniformLocation(getProgram(), "aspectRatio");
        
        setFractionalWidthOfAPixel(mWidth);              
        setDotScaling(mDot);  
        setAspectRatio(aspectRatio);
        Log.v("GENERATE_GPUIMAGE", "Width is  " + mOutputWidth);
        Log.v("GENERATE_GPUIMAGE", "Height is  " + mOutputHeight);
    }

    public void setDotScaling(final float scale) {
    		mDot = scale;
    		setFloat(dotScalingUniform, mDot);
    }
    
    public void setAspectRatio(final float ratio) {
    		setFloat(aspectRatioUniform, ratio);
    }

    public void setFractionalWidthOfAPixel(final float fraction) {
    		mWidth = fraction;	
    		setFloat(fractionalWidthOfAPixelUniform, mWidth); 
    }
    
}
