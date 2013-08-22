package dcpu.assembler.evaluate;

import java.util.Deque;

public class Negate extends Operator
{

	protected Negate()
	{
		super("~", 16);
	}

	@Override
	public boolean execute(Deque<Operand> stack)
	{
		if(stack.size() < 1)
			throw new IllegalArgumentException("Stack requires at least 1 operand!");
		Operand op1 = stack.peek();
		op1.m_iValue = ~op1.m_iValue; 
		return true;
	}

}
