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

package jp.co.cyberagent.android.gpuimage;

import android.opengl.GLES20;
import android.util.Log;

public class GPUImageTwoPassTextureSamplingFilter extends GPUImageTwoPassFilter {
    public GPUImageTwoPassTextureSamplingFilter(String firstVertexShader, String firstFragmentShader,
                                                String secondVertexShader, String secondFragmentShader) {
        super(firstVertexShader, firstFragmentShader,
                secondVertexShader, secondFragmentShader);
    }

    int texelWidthOffsetLocationFilterOne;
    int texelHeightOffsetLocationFilterOne;
    int texelWidthOffsetLocationFilterTwo;
    int texelHeightOffsetLocationFilterTwo;
    GPUImageFilter filterOne, filterTwo;
    float ratio;
    Boolean initialized = false;
    
    @Override
    public void onInit() {
        super.onInit();
        
        filterOne = mFilters.get(0);
        filterTwo = mFilters.get(1);
        
        texelWidthOffsetLocationFilterOne  = GLES20.glGetUniformLocation(filterOne.getProgram(), "texelWidthOffset");
        texelHeightOffsetLocationFilterOne = GLES20.glGetUniformLocation(filterOne.getProgram(), "texelHeightOffset");
        
        texelWidthOffsetLocationFilterTwo  = GLES20.glGetUniformLocation(filterTwo.getProgram(), "texelWidthOffset");
        texelHeightOffsetLocationFilterTwo = GLES20.glGetUniformLocation(filterTwo.getProgram(), "texelHeightOffset");
        
        initialized = true;
        initTexelOffsets();
    }

    protected void initTexelOffsets() {
        filterOne.setFloat(texelWidthOffsetLocationFilterOne, getHorizontalTexelOffsetRatio() / mOutputWidth);
        filterOne.setFloat(texelHeightOffsetLocationFilterOne, 0);

        filterTwo.setFloat(texelWidthOffsetLocationFilterTwo, 0);
        filterTwo.setFloat(texelHeightOffsetLocationFilterTwo, getVerticalTexelOffsetRatio() / mOutputHeight);
    }
    
    protected void updateHorizontalTexelOffsets(float f) {
    		if(initialized) {
    			filterOne.setFloat(texelWidthOffsetLocationFilterOne, f / mOutputWidth);
        		filterOne.setFloat(texelHeightOffsetLocationFilterOne, 0);
    		}
    }
    
    protected void updateVerticalTexelOffsets(float f) {
    		if(initialized) {
    			filterTwo.setFloat(texelWidthOffsetLocationFilterTwo, 0);
        		filterTwo.setFloat(texelHeightOffsetLocationFilterTwo, f / mOutputHeight);
    		}
    }


    @Override
    public void onOutputSizeChanged(int width, int height) {
        super.onOutputSizeChanged(width, height);
        initTexelOffsets();
    }

    public float getVerticalTexelOffsetRatio() {
        return 1f;
    }

    public float getHorizontalTexelOffsetRatio() {
        return 1f;
    }

	public void setVerticalTexelSpacing(float f) {
		updateVerticalTexelOffsets(f);
	}

	public void setHorizontalTexelSpacing(float f) {
		updateHorizontalTexelOffsets(f);
	}
}
