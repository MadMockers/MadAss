package dcpu;

import java.util.ArrayList;

public class Tools
{
	public static int parseLiteral(String in) throws NumberFormatException
	{
		in = in.trim();
		
		boolean negate = in.startsWith("~");
		if(negate)
			in = in.substring(1);
		
		int ret = -1;
		
		boolean found = false;
		
		if(in.startsWith("0x"))
		{
			ret = Integer.parseInt(in.substring(2), 16);
			found = true;
		}
		else if(in.startsWith("'") && in.endsWith("'") && in.length() == 3)
		{
			ret = (int) in.charAt(1);
			found = true;
		}
		else
		{
			ret = Integer.parseInt(in);
			found = true;
		}
		
		if(!found)
		{
			throw new IllegalArgumentException("Unknown literal '" + in + "'");
		}
		
		if(negate)
			ret = ~ret;
		
		return ret;
	}
	
	public static String[] split(String line)
	{
		if(line.trim().length() == 0)
			return new String[0];
		
		char[] chars = line.toCharArray();
		
		boolean inQuote = false;
		
		char closing = 0;
		
		ArrayList<String> parts = new ArrayList<String>();
		
		int prev = 0;
		
		for(int i = 0;i < chars.length;i++)
		{
			if(!inQuote && (chars[i] == '\"' || chars[i] == '\'' || chars[i] == '['))
			{
				prev = i;
				if(chars[i] == '[')
					closing = ']';
				else
					closing = chars[i];
				
				inQuote = true;
			}
			else if(inQuote && chars[i] == closing)
			{
				inQuote = false;
				
				parts.add(line.substring(prev, i + 1));
				
				prev = i + 1;
			}
			else if((chars[i] == ' ' || chars[i] == '\t' || chars[i] == ',') && !inQuote)
			{
				if(prev == i)
				{
					prev = i + 1;
					continue;
				}
				
				parts.add(line.substring(prev, i).trim());
				
				prev = i + 1;
			}
			else if(chars[i] == ';')
				break;
		}
		
		if(inQuote)
		{
			throw new IllegalArgumentException("No closing quote.");
		}
		if(prev < line.length())
			parts.add(line.substring(prev, line.length()));
		
		return parts.toArray(new String[parts.size()]);
	}
	
	public static int[] convertDat(String line)
	{
		String[] split = split(line);
		
		ArrayList<Integer> data = new ArrayList<Integer>();
		
		for(int i = 0;i < split.length;i++)
		{
			String sDat = split[i];
			
			try
			{
				int literal = parseLiteral(sDat);
				data.add(literal);
				continue;
			}
			catch(Exception e)
			{
				// not a literal, continue...
			}
			
			if(sDat.charAt(0) == '[')
			{
				System.out.println("Ignoring DAT surrounded by '[' ']'");
				continue;
			}
			
			if(sDat.charAt(0) == '\'')
			{
				sDat = sDat.substring(1, sDat.length() - 1);
				
				for(byte c : sDat.getBytes())
				{
					data.add((c & 0xFF));
				}
				continue;
			}
		}
		
		int[] out = new int[data.size()];
		
		for(int i = 0;i < out.length;i++)
			out[i] = data.get(i);
		
		return out;
	}
	
	public static void main(String[] args)
	{
		String a = ".define test\t\t\t1 ";
		String[] split = split(a);
		int b = 5;
	}
	
}
