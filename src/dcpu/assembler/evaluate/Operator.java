package dcpu.assembler.evaluate;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;

abstract public class Operator extends Entity
{
	
	private String m_sSymbol;
	protected int m_iPriority;
	
	protected Operator(String sym, int priority)
	{
		m_sSymbol = sym;
		m_iPriority = priority;
	}
	
	String getSymbol()
	{
		return m_sSymbol;
	}
	
	public boolean hasPriorityOver(Operator op)
	{
		return m_iPriority >= op.m_iPriority;
	}
	
	public boolean mutuallyKillOperator(Operator op)
	{
		// to be over-ridden - specifically for OpenBracket to kill CloseBracket operators
		return false;
	}
	
	abstract public boolean execute(Deque<Operand> stack);
	
	public static int MAX_PRIORITY = 128;
	
	private static HashMap<String, Operator> m_vOperators;
	private static ArrayList<String> m_vOperatorSymbols;
	
	public static Operator getOperator(String symbol)
	{
		return m_vOperators.get(symbol);
	}
	
	public static int isOperator(String in, int index)
	{
		for(String sym : m_vOperatorSymbols)
		{
			if(sym.length() > in.length() - index)
				continue;
			
			boolean found = true;
			for(int i = 0;i < sym.length();i++)
			{
				if(sym.charAt(i) != in.charAt(i + index))
				{
					found = false;
					break;
				}
			}
			if(found)
				return sym.length();
		}
		return 0;
	}
	
	private static void registerOperator(Operator o)
	{
		m_vOperators.put(o.getSymbol(), o);
		m_vOperatorSymbols.add(o.getSymbol());
	}
	
	static
	{
		m_vOperators = new HashMap<String, Operator>();
		m_vOperatorSymbols = new ArrayList<String>();
		
		registerOperator(new Addition());
		registerOperator(new Subtraction());
		registerOperator(new Multiplication());
		registerOperator(new Division());
		registerOperator(new Negate());
		registerOperator(new OpenBracket());
		registerOperator(new CloseBracket());
		registerOperator(new BinaryAnd());
		registerOperator(new BinaryOr());
		registerOperator(new Xor());
		registerOperator(new LeftShift());
		registerOperator(new RightShift());
		registerOperator(new LogicalRightShift());
	}
	
}
