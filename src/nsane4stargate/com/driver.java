package nsane4stargate.com;
/***********************************************************
 * Author: Lea Middleton								   *
 * AES encryption      									   *
 * 02.27.2019											   *
 ***********************************************************/
import java.util.*;

public class driver {

	public static void main(String[] args) {
		
		String plainText = "32 43 f6 a8 88 5a 30 8d 31 31 98 a2 e0 37 07 34";
		
		String key_schedule[] = { "0x2b7e151628aed2a6abf7158809cf4f3c",
								  "0xa0fafe1788542cb123a339392a6c7605",
								  "0xf2c295f27a96b9435935807a7359f67f",
								  "0x3d80477d4716fe3e1e237e446d7a883b",
								  "0xef44a541a8525b7fb671253bdb0bad00",
								  "0xd4d1c6f87c839d87caf2b8bc11f915bc",
								  "0x6d88a37a110b3efddbf98641ca0093fd",
								  "0x4e54f70e5f5fc9f384a64fb24ea6dc4f",
								  "0xead27321b58dbad2312bf5607f8d292f",
								  "0xac7766f319fadc2128d12941575c006e",
								  "0xd014f9a8c9ee2589e13f0cc8b6630ca6"};
		
		String [][] state = new String[4][4];
		
		Conversions convert = new Conversions();
		
		Layers layer = new Layers();
		
		/* Create a Scanner to scan plainText */
		Scanner user_input = new Scanner(plainText);
		
		int row = 0, col = 0;
	
		/* Add plain text to 2d array */
		while(user_input.hasNext()) {
			plainText = user_input.next();
			convert.add_to_State(plainText, row, col, state);
			if(row < 4) {
				row++;
			}
			if (row == 4){
				row = 0;
				col++; 
			}
		}
		
		System.out.println("== Plaintext ==");
		convert.print(state);
		
		/* Initial round */
		layer.initalRound(state,key_schedule,convert);
		System.out.println("== Round " + 1 + " ==");
		convert.print(state);
			
		/* Start of 10 rounds */
		for(int round = 1; round <  key_schedule.length; round++) {
			layer.subBytes(state, convert);
			System.out.println("-- After subBytes --");
			convert.print(state);

			layer.shiftRows(state, 3);
			System.out.println("-- After shiftRows --");
			convert.print(state);

			if(round < 10) {
				layer.mixColumns(state, convert.get_Galois(), convert);
				System.out.println("-- After mixColumns --");
				convert.print(state);
			}
			convert.to_Key_Matrix(key_schedule[round]);
			layer.addRoundKey(state,convert.get_Key_Matrix());
			
			if(round < 10) {
				int r = round + 1;
				System.out.println("== Round "+ r + " ==");
				convert.print(state);
			}else {
				System.out.println("== CipherText ==");
				convert.print(state);
			}
		}
		user_input.close();
	}
}
