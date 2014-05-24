package dcpu.assembler.evaluate;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import dcpu.assembler.Assembler;
import dcpu.assembler.entities.Label;
import dcpu.assembler.entities.Literal;

public class Evaluator
{

	public static int evaluate(Assembler as, String in)
	{
		Evaluator e = new Evaluator(as, in);
		e.solve();
		return e.operandStack.pop().m_iValue;
	}
	
	Assembler assembler;
	Deque<Operand> operandStack;
	Deque<Operator> operatorStack;
	
	Queue<Entity> entityQueue;
	public Evaluator(Assembler as, String in)
	{
		assembler = as;
		operandStack = new ArrayDeque<Operand>();
		operatorStack = new ArrayDeque<Operator>();
		entityQueue = new LinkedList<Entity>();
		
		populateQueue(in);
	}
	
	private void solve()
	{
		while(!entityQueue.isEmpty())
		{
			Entity ent = entityQueue.remove();
			
			if(ent instanceof Operand)
				operandStack.push((Operand) ent);
			else if(ent instanceof Operator)
			{
				boolean pushOperator = true;
				Operator op = (Operator) ent;
				Operator top = operatorStack.peek();
				while(top != null && top.hasPriorityOver(op))
				{
					if(top.mutuallyKillOperator(op))
					{
						operatorStack.remove();
						pushOperator = false;
						break;
					}
					if(top.execute(operandStack))
					{
						operatorStack.remove();
						top = operatorStack.peek();
					}
					else
						break;
				}
				if(pushOperator)
					operatorStack.push(op);
			}
		}
		
		while(!operatorStack.isEmpty())
		{
			Operator op = operatorStack.remove();
			op.execute(operandStack);
		}
	}
	
	private void populateQueue(String in)
	{
		int lastOperandStart = -1;
		for(int i = 0;i < in.length();i++)
		{
			int operatorLen = Operator.isOperator(in, i);
			if(operatorLen > 0)
			{
				if(lastOperandStart != -1)
				{
					String operand = in.substring(lastOperandStart, i).trim();
					
					int value;
					if(operand.length() > 0)
					{
						if(assembler != null)
						{
							Literal l = assembler.parseLiteral(operand, true, false);
							/*if(l == null || (l instanceof Label && ((Label) l).isUnknown()))
							{
								throw new IllegalArgumentException("Unknown literal '" + operand + "'");
							}*/
							value = l.getValue();
						}
						else
						{
							value = Integer.parseInt(operand);
						}
						entityQueue.add(new Operand(value));
					}
				}
				lastOperandStart = i + operatorLen;
				entityQueue.add(Operator.getOperator(in.substring(i, i + operatorLen)));
			}
			if(lastOperandStart == -1)
				lastOperandStart = i;
		}
		if(lastOperandStart >= 0 && lastOperandStart < in.length())
		{
			String operand = in.substring(lastOperandStart, in.length()).trim();
			addOperand(operand);
		}
	}
	
	private void addOperand(String operand)
	{
		int value;
		if(operand.length() > 0)
		{
			if(assembler != null)
			{
				Literal l = assembler.parseLiteral(operand, true, false);
				if(l == null || (l instanceof Label && ((Label) l).isUnknown()))
					throw new IllegalArgumentException("Unknown literal '" + operand + "'");
				value = l.getValue();
			}
			else
			{
				value = Integer.parseInt(operand);
			}
			entityQueue.add(new Operand(value));
		}
	}
	
}
