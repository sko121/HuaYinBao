package com.thtfit.pos.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class Config
{
	private static String filePath = Environment.getExternalStorageDirectory() + File.separator + "THTFIT"
			+ File.separator + "config.ini";
	private static List<String[]> saveOption = new ArrayList<String[]>();

	// 读取内容
	private static String loadValue(String optionName)
	{
		String result = "";
		File configFile = new File(filePath);
		// 文件不存在
		if (!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
			}
		}
		else
		{
			try
			{
				FileReader fileReader = new FileReader(filePath);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String tmpLine = null;
				while ((tmpLine = bufferedReader.readLine()) != null)
				{
					if (tmpLine.indexOf(optionName + "=") == 0)
					{
						result = tmpLine.substring(tmpLine.indexOf(optionName + "=") + optionName.length() + 1);
					}
				}
				bufferedReader.close();
				bufferedReader = null;
				fileReader.close();
				fileReader = null;
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return result;
	}

	public static String get(String optionName)
	{
		return get(optionName, "");
	}

	public static String get(String optionName, String defaultValue)
	{
		String tmpValue = loadValue(optionName);
		if (tmpValue.length() == 0)
		{
			return defaultValue;
		}
		else
		{
			return tmpValue;
		}
	}

	public static long getLong(String optionName)
	{
		long result = 0;
		try
		{
			result = Long.valueOf(loadValue(optionName));
		}
		catch (Exception e)
		{
		}
		return result;
	}

	public static void addSaveOption(String optionName, String optionValue)
	{
		String[] array = { optionName, optionValue };
		saveOption.add(array);
	}

	public static void clearOption()
	{
		File configFile = new File(filePath);
		// 文件不存在
		if (configFile.isFile())
		{
			try
			{
				configFile.delete();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	synchronized public static void save()
	{
		File configFile = new File(filePath);
		// 文件不存在
		if (!configFile.exists())
		{
			try
			{
				configFile.createNewFile();
			}
			catch (IOException e)
			{
			}
		}
		else
		{
			try
			{
				// 读取文件内容
				String newFileData = "";
				FileReader fileReader = new FileReader(filePath);
				BufferedReader bufferedReader = new BufferedReader(fileReader);
				String tmpLine = null;

				// 读取本次保存值以外的数据
				while ((tmpLine = bufferedReader.readLine()) != null)
				{
					boolean inFile = false;
					for (int i = 0; i < saveOption.size(); i++)
					{
						if (tmpLine.indexOf(saveOption.get(i)[0] + "=") == 0)
						{
							inFile = true;
							break;
						}
					}
					if (!inFile && tmpLine.length() > 0)
					{
						newFileData += tmpLine + "\n";
					}
				}

				// 增加本次保存的数据
				for (int i = 0; i < saveOption.size(); i++)
				{
					newFileData += saveOption.get(i)[0] + "=" + saveOption.get(i)[1] + "\n";
				}

				// 清空临时数据
				saveOption.clear();
				bufferedReader.close();
				bufferedReader = null;
				fileReader.close();
				fileReader = null;
				// System.out.println("cf:" + newFileData);

				// 保存文件内容
				FileWriter fileWriter = new FileWriter(filePath);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(newFileData);
				bufferedWriter.newLine();
				bufferedWriter.flush();
				bufferedWriter.close();
				bufferedWriter = null;
				fileWriter.close();
				fileWriter = null;
			}
			catch (IOException e)
			{
			}
		}
	}
}
