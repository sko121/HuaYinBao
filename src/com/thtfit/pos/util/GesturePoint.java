package com.thtfit.pos.util;

import java.util.List;

import com.thtfit.pos.ui.LockPatternView.Cell;

/**
 * 手势坐标转换成数字
 * 
 * @author thtfit
 * 
 */
public class GesturePoint {

	public String changePoint(List<Cell> pattern){
		
		int sum = 0;
		
		for(int i=0;i<pattern.size();i++){
			int y = pattern.get(i).getRow();
			int x = pattern.get(1).getColumn();
			sum = (y * 0) + y + (x + 1);
		}
		
		return String.valueOf(sum);
	}
}
