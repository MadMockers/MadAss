package dcpu.assembler.entities;

import dcpu.assembler.Assembler;

public abstract class OutputEntity extends CoreEntity
{

	public OutputEntity(Assembler a, int lineN, int pc, String line)
	{
		super(a, lineN, pc, line);
	}
	
	abstract public int[] getData();

}
