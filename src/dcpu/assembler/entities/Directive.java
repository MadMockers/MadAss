package dcpu.assembler.entities;

import java.util.ArrayList;

import dcpu.Tools;
import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.directives.DirectiveHandler;

public class Directive extends CoreEntity
{

	DirectiveHandler m_Handler;
	
	String[] m_aArgs;
	
	String m_sName;
	
	public Directive(Assembler a, int lineN, int pc, String line, DirectiveHandler dh, String name, String[] args)
	{
		super(a, lineN, pc, line);
		
		m_Handler = dh;
		
		m_sName = name;
		
		/*ArrayList<String> args = new ArrayList<String>();
		
		StringBuffer arg = new StringBuffer();
		
		char[] chars = params.toCharArray();
		
		boolean inQuotes = false;
		for(int i = 0;i < params.length();i++)
		{
			if(chars[i] == '\\')
			{
				arg.append(chars[i + 1]);
				i++;
				continue;
			}
			if(chars[i] == '\"')
			{
				inQuotes = !inQuotes;
				continue;
			}
			if(!inQuotes)
			{
				if(!inQuotes)
				{
					if((chars[i] == ' ' || chars[i] == '\t') && arg.length() > 0)
					{
						args.add(arg.toString().trim());
						arg = new StringBuffer();
						continue;
					}
				}
			}
			arg.append(chars[i]);
		}
		
		if(arg.length() > 0)
			args.add(arg.toString());
		
		*/
		
		m_aArgs = args;
		if(dh.getArgumentCount() != -1 && m_aArgs.length != dh.getArgumentCount())
		{
			throw new IllegalArgumentException(
					"Directive '" + name + "' expected " + dh.getArgumentCount() + 
					" arguments, but got " + m_aArgs.length + " arguments.");
		}
	}
	
	public CoreEntity[] handleDirective(ParserState state)
	{
		return m_Handler.handleDirective(m_Assembler, state, m_aArgs);
	}
	
}
