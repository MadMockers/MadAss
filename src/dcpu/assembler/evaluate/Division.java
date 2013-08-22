package dcpu.assembler.evaluate;

import java.util.Deque;


public class Division extends Operator
{

	protected Division()
	{
		super("/", 1);
	}

	@Override
	public boolean execute(Deque<Operand> stack)
	{
		if(stack.size() < 2)
			throw new IllegalArgumentException("Stack requires at least 2 operands!");
		Operand op2 = stack.pop();
		Operand op1 = stack.pop();
		stack.push(new Operand(op1.m_iValue / op2.m_iValue));
		return true;
	}

}
