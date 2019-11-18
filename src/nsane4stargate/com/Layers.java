package nsane4stargate.com;

import java.math.BigInteger;

public class Layers {
	
	public Layers() {}

	
	void initalRound(String[][] state, String[] key_schedule, Conversions convert) {
		convert.to_Key_Matrix(key_schedule[0]);
		addRoundKey(state,convert.get_Key_Matrix());
	}
	
	void subBytes(String[][] state, Conversions convert) {
		int r = 0, c = 0, trade_off = 0;
		for(int row = 0; row < state.length; row ++) {
			for(int column = 0; column < state[row].length; column++) {
				if(!Character.isDigit(state[row][column].charAt(0)) || !Character.isDigit(state[row][column].charAt(1))){
					/* Check the first character */
					if(!Character.isDigit(state[row][column].charAt(0))){
						r = Integer.parseInt(state[row][column].substring(0,1),16);
					}else {
						r = Integer.parseInt(state[row][column].substring(0,1));
					}
					/* Check the second character */
					if(!Character.isDigit(state[row][column].charAt(1))) {
						c = Integer.parseInt(state[row][column].substring(1,2),16);
					}else {
						c = Integer.parseInt(state[row][column].substring(1,2));
					}
					
				}else {
					r = Integer.parseInt(state[row][column].substring(0,1));
					c = Integer.parseInt(state[row][column].substring(1,2));
				}
				trade_off = convert.get_SBox()[r][c];
				state[row][column] = Integer.toHexString(trade_off);
			}
		}
	}
	
	void addRoundKey(String[][] state, String[][] key_matrix) {
		for(int row = 0; row < state.length; row ++) {
			for(int column = 0; column < state[row].length; column ++){
				BigInteger s = new BigInteger(state[row][column],16);
				BigInteger k = new BigInteger(key_matrix[row][column],16);
				BigInteger XORed = s.xor(k);
				String results = "";
			
				/* If the result from the xor operation yield 1 character, add 0 to the beginning */
				if(XORed.toString(16).length() == 1) {
					results  += "0"+ XORed.toString(16);
				}else {
					 results  += XORed.toString(16);
				}
				state[row][column] = results;
			}
		}
	}

	void shiftRows(String[][] state, int row) {
		String temp[] = new String[row];
		int shifts = row;
		if(row == 0) {
			return;
		}else{
			for(int i = 0; i < shifts; i++) {
				temp[i] = state[row][0];
				state[row][0] = state[row][1];
				state[row][1] = state[row][2];
				state[row][2] = state[row][3];
				state[row][3] = temp[i];
			}
		}
		shiftRows(state, row-1);
	}

	void mixColumns(String[][] state, int[][] galois, Conversions convert) {
		String[][] r = new String[4][4];
		String[] temp = new String[4];
		int r_row = 0, r_column = 0;
		
		for(int column = 0; column < state[0].length; column++) {
			for(int row = 0; row < state.length; row ++) {
				temp[row] = state[row][column];
			}
			mixColumnAux(temp,galois,r,convert,r_row,r_column);
			r_column++;
		}
		
		/* Copy data from r[][] to state[][] */
		for(int row = 0; row < r.length; row++) {
			for(int column = 0; column < r[row].length; column++) {
				state[row][column] = r[row][column];
			}
		}
	}
	
	void mixColumnAux(String[]state, int[][] galois, String[][] r, Conversions convert, int r_row, int r_column){
		int r_element = 0, choice, round = 1, num;
		String results = "0";
		for(int row = 0; row < galois.length; row ++) {
			for (int column = 0; column < galois[row].length; column ++) {
				choice = galois[row][column];
				num = Integer.parseInt(state[column],16);
				switch(choice) {
				case 1:
					if(round == 1) {
						r_element = num;
					}else {
						r_element ^= num;
					}
					break;
				case 2:
					if(round == 1 ) {
						r_element = convert.multiply_by_2(num);
					}else {
						r_element ^= convert.multiply_by_2(num);
					}
					break;
				case 3:
					if(round == 1) {
						r_element = convert.multiply_by_3(num);
					}else {
						r_element ^= convert.multiply_by_3(num);
					}
					break;
				}
				round++;
			}
			if(String.valueOf(r_element).length() == 1) {
				results += Integer.toHexString(r_element);
			}else {
				results = Integer.toHexString(r_element);
			}
			
			r[r_row][r_column] = results;
			r_row ++;
			
			/* Reset important variable for next matrix multiplication */
			r_element = 0;
			round = 1;
			results = "0";
		}
	}
}
