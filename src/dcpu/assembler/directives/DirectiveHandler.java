package dcpu.assembler.directives;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import dcpu.assembler.Assembler;
import dcpu.assembler.Assembler.ParserState;
import dcpu.assembler.entities.CoreEntity;
import dcpu.assembler.entities.Literal;

public abstract class DirectiveHandler
{
	
	private final String[] m_aAlias;
	private final int m_iArgumentCount;
	
	protected DirectiveHandler(int argCount, String... alias)
	{
		if(alias.length == 0)
			throw new IllegalArgumentException("Must have at least 1 alias");
		m_aAlias = alias;
		m_iArgumentCount = argCount;
	}
	
	public int getArgumentCount()
	{
		return m_iArgumentCount;
	}
	
	public abstract Literal resolveUnknown(String in);
	public abstract CoreEntity[] handleDirective(Assembler a, ParserState state, String[] args);
	
	private static HashMap<String, DirectiveHandler> g_vHandlers = new HashMap<String, DirectiveHandler>();
	
	private static void registerHandler(DirectiveHandler d)
	{
		for(String a : d.m_aAlias)
			g_vHandlers.put(a.toLowerCase(), d);
	}
	
	public static Iterator<DirectiveHandler> iterator()
	{
		return g_vHandlers.values().iterator();
	}
	
	public static DirectiveHandler getDirectiveHandler(String name)
	{
		return g_vHandlers.get(name);
	}
	
	public static void registerHandlers()
	{
		registerHandler(new Define());
		registerHandler(new Ascii());
		registerHandler(new Asciiz());
		registerHandler(new Fill());
		registerHandler(new Reserve());
		registerHandler(new Dat());
	}
	
	static
	{
		registerHandlers();
	}
	
}
