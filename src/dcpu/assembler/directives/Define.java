package dcpu.assembler.directives;

import java.util.HashMap;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Literal;

public class Define extends DirectiveHandler
{

	private HashMap<String, Literal> m_vDefines = new HashMap<String, Literal>();
	
	protected Define()
	{
		super(-1, "define", "equ");
	}

	@Override
	public CoreEntity[] handleDirective(Assembler a, ParserState state, String[] args)
	{
		if(args.length < 1)
			throw new IllegalArgumentException("Directive 'define' expected at least 1 argument, but got " + args.length);
		String name = args[0];
		String value = "1";
		if(args.length == 2)
			value = args[1];
		
		m_vDefines.put(name.toLowerCase(), a.parseLiteral(value));
		
		return null;
	}

	@Override
	public Literal resolveUnknown(String in)
	{
		return m_vDefines.get(in.toLowerCase());
	}
	
	public void undefine(String in)
	{
		m_vDefines.remove(in);
	}

}