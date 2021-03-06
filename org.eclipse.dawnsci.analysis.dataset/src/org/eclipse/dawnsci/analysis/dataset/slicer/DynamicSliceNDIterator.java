/*-
 * Copyright 2015 Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.eclipse.dawnsci.analysis.dataset.slicer;

import org.eclipse.january.dataset.Dataset;
import org.eclipse.january.dataset.DatasetUtils;
import org.eclipse.january.dataset.IDataset;
import org.eclipse.january.dataset.ShapeUtils;
import org.eclipse.january.dataset.SliceND;

public class DynamicSliceNDIterator {

	private int[] scanShape;
	private Dataset key;
	private int absCurrentPosition = -1;
	private int currentMax = 0;
	private SliceND currentSlice;
	
	private boolean repeating = false;
	private long currentkey;
	private int count = 0;
	
	public DynamicSliceNDIterator(int[] initialShape, IDataset key, int scanRank) {
		scanShape = new int[scanRank];
		this.key = DatasetUtils.convertToDataset(key);
		updateScanShape(initialShape);
		buildInitialSlice(initialShape,initialShape.length - scanRank);
	}
	
	private void buildInitialSlice(int[] initialShape, int dataRank) {
		int[] maxShape = initialShape.clone();
		int[] shape = initialShape.clone();
		for (int i = 0; i < maxShape.length - dataRank; i++){
			maxShape[i] = -1;
			shape[i] = 1;
		}
		currentSlice = new SliceND(shape, maxShape);
		for (int i = 0; i < maxShape.length - dataRank; i++) currentSlice.setSlice(0, 0, 1, 1);
		
		
	}
	
	private void updateScanShape(int[] initialShape){
		//change for shape of 0?
		System.arraycopy(initialShape, 0, scanShape, 0, scanShape.length);
		currentMax = scanShape[0] == 0 ? 1 : scanShape[0]; 
		for (int i = 1; i < scanShape.length; i++){
			currentMax *= scanShape[i] == 0 ? 1 : scanShape[i]; 
		}
		currentMax--;
	}
	
	public void updateShape(int[] shape, IDataset key) {
		updateScanShape(shape);
		this.key = DatasetUtils.convertToDataset(key);
	}
	
	private void updateCurrentSlice() {
		int[] pos = ShapeUtils.getNDPositionFromShape(absCurrentPosition,scanShape);
		for (int i = 0; i < pos.length; i++) currentSlice.setSlice(i, pos[i], pos[i]+1, 1);
	}
	
	public SliceND getCurrentSlice() {
		if (absCurrentPosition == -1) throw new IllegalArgumentException("Current position is at -1!, has hasNext been called?");
		return currentSlice;
	}
	
	public boolean hasNext() {
		
		//if repeating, process current position if key different
		if (repeating && absCurrentPosition >= 0) {
			
			long k = key.getElementLongAbs(absCurrentPosition);
			
			if (k != currentkey) {
				currentkey = key.getElementLongAbs(absCurrentPosition);
				return true;
			}
		}
		
		if (absCurrentPosition == currentMax) return false;
		
		absCurrentPosition++;
		if ( key.getSize() <= absCurrentPosition || key.getElementLongAbs(absCurrentPosition) == 0) {
			absCurrentPosition--;
			return false;
		}
		
		if (repeating) currentkey = key.getElementLongAbs(absCurrentPosition);
		
		updateCurrentSlice();
		count++;
		return true;
	}
	
	public boolean peekHasNext() {
		
		if (repeating && absCurrentPosition >= 0) {
			long k = key.getElementLongAbs(absCurrentPosition);
			
			if (k != currentkey) return true;
		}
		
		
		if (absCurrentPosition == currentMax) return false;
		
		if (key.getSize() <= absCurrentPosition +1 ) return false;
		
		return key.getElementLongAbs(absCurrentPosition + 1) != 0;
	}
	
	public void reset() {
		absCurrentPosition = -1;
		count = 0;
		currentkey = 0;
	}
	
	public int getCurrentMax() {
		return currentMax;
	}
	
	public void enableRepeating() {
		repeating = true;
	}

	public int getCount() {
		return count;
	}
	
}
