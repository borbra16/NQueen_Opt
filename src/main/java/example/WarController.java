package com.example.controller;


import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WarController.WAR_BASE_URI)
public class WarController {
	
	public static final String WAR_BASE_URI = "startProcess";
	static int count = 0;
	static int[][] field = new int[0][0];
	static List<Double> logList = new ArrayList<Double>();
	static List<Integer> fieldList = new ArrayList<Integer>();
	
	static StringBuilder fileBuilder = new StringBuilder();
	
	static List<String> execList = new ArrayList<String>();	
	
	static FileOutputStream fos;
	
	
	@RequestMapping(value = "/startGame")
	@ResponseBody
	public String startGame()
	{
		StringBuilder sb = null;
	
		int i = 0;
		int z = 0;
		
		int row = 0;
		int col = 0;
	
		int N = field.length;
		
		StopWatch stopWatch = null;
	
		double recordedVal = 0;
		
		try
		{
		
			count++;
			stopWatch = new StopWatch("Stop Watch");
		
			sb = new StringBuilder();
			
			if(N <= 0)
			{
				return "Field is Empty! please set fieldValue!";
			}
			else if(count == 1)
			{
				fileBuilder.append("FieldSize");
				fileBuilder.append(";");
				fileBuilder.append("Execution Time (ms)");
				fileBuilder.append("\n");
			}
			else if(count > 1)
			{
				for(int[] n : field)
				{
					for(int x : n)
					{
						field[z][i] = 0;
						z++;
					}
					z=0;
					i++;
				}
			}
			
			//start timer
			stopWatch.start();
			//Condition to stay in the while-loop
			while((col >= 0) && (col<N))
			{
				while((row<N) && (conflict(row,col) != 0))
				{
					row ++;
				}
				
				if(row < N)
				{
					setPosOfQueen(row,col);
					col++;
					row = 0;
				}
				else
				{
					col--;
					
					if(col < 0)
					{
						break;
					}
						
					row = getPosOfQueen(col);
					removeQueen(row,col);
					row++;
				}
			}
			stopWatch.stop();
			recordedVal = stopWatch.getTotalTimeMillis();
			
			logList.add(recordedVal);
			fieldList.add(N);
			
			//log Data and Write to File
			fileBuilder.append(N);
			fileBuilder.append(";");
			fileBuilder.append(recordedVal);
			fileBuilder.append("\n");
			
			byte[] result = fileBuilder.toString().getBytes();
			
			fos = new FileOutputStream("/usr/LogFile.txt");
			fos.write(result);
			fos.close();
			
			
			if(col >= N)
			{
				sb.append("A solution exists!");
				sb.append("<br />");
				sb.append("Field length: " + N);
				sb.append("<br />");
				sb.append(stopWatch.shortSummary());
				sb.append("<br />"); 
				sb.append("<br />");
				sb.append(printSolution());
				
				return sb.toString();
				
			}
			else
			{
				return "There is no solution!";
			}
		}
		catch(Exception ex)
		{
			System.out.println(ex.getMessage());
		}
		
		return null;
	}
	
	
	public int getPosOfQueen(int col)
	{
		int row = 0;
		int N = field.length;
		for(row = 0; row < N; row ++)
		{
			
			if(field[row][col] == 1)
			{
				return row;
			}
			
		}
		
		return -1;
	}
	
	
	@RequestMapping(value = "/writeToFile")
	@ResponseBody
	public void writeFile()
	{
		try 
		{
		
		} 
		catch (Exception ex) 
		{
			System.out.println("Error while writing to file!");
		}
		
		
	}
	
	
	
	@RequestMapping(value = "/getTime")
	@ResponseBody
	public String getRecordedTime()
	{
		StringBuilder sb = new StringBuilder();
		
		int index = 0;
		for(double d : logList)
		{
			sb.append("FieldSize: " + fieldList.get(index) + "   ");
			sb.append("Execution Time: " + logList.get(index) + " (ms)");
			index++;
			sb.append("<br />");
		}
		
		return sb.toString();
	}
	
	
	
	@RequestMapping(value = "setField/{fieldVal}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String setField(@PathVariable("fieldVal") int fieldVal)
	{
		StringBuilder sb = new StringBuilder();
		int arrayLength = 0;
		
		field = new int[fieldVal][fieldVal];
		arrayLength = field.length;
		
		sb.append("FieldArray is set to N: " + fieldVal);
		sb.append("\n");
		sb.append("FieldLength= " + arrayLength);
		
		return sb.toString();
	
	}
	
	@RequestMapping(value = "setQueenToPos/{row}/{col}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String setPosOfQueen(@PathVariable("row")int row, @PathVariable("col")int col)
	{
		StringBuilder sb = new StringBuilder();
		
		
		if(field[row][col] == 1)
		{
			return "Queen already set!";
		}
		field[row][col] = 1;
		
		sb.append("Queen successfully set to Position: ");
		sb.append(" " + "row: " + row);
		sb.append(" " + "col: " + col);
		
		return sb.toString();
	}
	
	@RequestMapping(value = "removeQueen/{row}/{col}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	@ResponseBody
	public String removeQueen(@PathVariable("row")int row, @PathVariable("col")int col)
	{
		StringBuilder sb = new StringBuilder();
		
		
		if(field[row][col] == 0)
		{
			return "Queen already removed or Field was empty!";
		}
		field[row][col] = 0;
		
		sb.append("Queen successfully removed from Position: ");
		sb.append(" " + "row: " + row);
		sb.append(" " + "col: " + col);
		
		return sb.toString();
	}
	
	
	public int conflict(int row, int col)
	{
		int r1 = 0;
		int c1 = 0;
		
		int length = field.length;
		
		for(c1 = col; c1 >=0; c1--)
		{
			if(field[row][c1] == 1)
			{
				return 1;
			}
		}
		
		
		for(r1=row, c1=col; r1<length && c1>=0; r1++,c1--)
		{
			if(field[r1][c1] == 1)
			{
				return 1;
			}
		}
		
		for(r1=row,c1=col; r1>=0 && c1>=0; r1--,c1--)
		{
			if(field[r1][c1] == 1)
			{
				return 1;
			}
		}

		return 0;
	}
	
	public String printSolution()
	{
		StringBuilder sb = new StringBuilder();
		
		int N = field.length;
		
		for(int row=0; row<N; row++)
		{
			for(int col=0; col<N; col++)
			{
				if(field[row][col] == 1)
				{
					sb.append("<font size=\"3\" color=\"red\">" + field[row][col] + "</font>");
				}
				else
				{
					sb.append(field[row][col]);
				}
			}
			
			sb.append("<br />");
		}

		return sb.toString();
		
	}
}

