/*
 * Copyright (C) 2004-2008  exedio GmbH (www.exedio.com)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package com.exedio.cope.util;

import java.util.Arrays;

public final class SequenceChecker
{
	private final int length;
	
	private final boolean[] buffer;
	private int bufferNext = 0;
	
	private boolean afterFirst = false;
	private int firstNumber;
	private int maxNumber;
	
	private volatile int countInOrder = 0;
	private volatile int countOutOfOrder = 0;
	private volatile int countDuplicate = 0;
	private volatile int countLost = 0;
	private volatile int countLate = 0;
	
	public SequenceChecker(final int length)
	{
		if(length<1)
			throw new IllegalArgumentException("length must be greater than zero, but was " + length);
		
		this.length = length;
		buffer = new boolean[length];
		Arrays.fill(buffer, true);
		//System.out.println("----------------");
	}
	
	public int getLength()
	{
		return length;
	}
	
	/*private String toStringInternal()
	{
		final StringBuilder result = new StringBuilder();
		result.append('[');
		for(int i = 0; i<buffer.length; i++)
		{
			result.append((i==bufferNext) ? '>' : ' ');
			result.append(buffer[i] ? '+' : '.');
		}
		result.append(']');
		return result.toString();
	}*/
	
	/**
	 * @return whether the given number is a duplicate
	 */
	public final boolean check(final int number)
	{
		//System.out.println("-----------" + number + "----" + toStringInternal());
		if(afterFirst)
		{
			if(number>maxNumber)
			{
				//System.out.println("-----------" + number + "----countInOrder");
				final int todo = number - maxNumber;
				for(int i = 0; i<todo; i++)
				{
					if(!buffer[bufferNext])
						countLost++;
					buffer[bufferNext] = (i==(todo-1));
					
					bufferNext++;
					if(bufferNext>=length)
						bufferNext = 0;
				}
				maxNumber = number;
				
				countInOrder++;
				return false;
			}
			else if(number<firstNumber)
			{
				//System.out.println("-----------" + number + "----ignore");
				// forget, since I don't know anything about numbers before firstNumber
				return false;
			}
			else if(number>(maxNumber-length))
			{
				int pos = bufferNext - (maxNumber-number) - 1;
				if(pos<0)
					pos += length;
				
				//System.out.println("-----------" + number + "----outOfOrder or duplicate ---- on " + pos);
				if(buffer[pos])
				{
					//System.out.println("-----------" + number + "----duplicate");
					countDuplicate++;
					return true;
				}
				else
				{
					//System.out.println("-----------" + number + "----outOfOrder");
					buffer[pos] = true;
					
					countOutOfOrder++;
					return false;
				}
			}
			else
			{
				countLate++;
				return false;
			}
		}
		else
		{
			afterFirst = true;
			firstNumber = number;
			maxNumber = number;
			
			countInOrder++;
			return false;
		}
	}

	public int getFirstNumber()
	{
		if(!afterFirst)
			throw new IllegalStateException("did not yet check first number");
		
		return firstNumber;
	}

	public int getMaxNumber()
	{
		if(!afterFirst)
			throw new IllegalStateException("did not yet check first number");
		
		return maxNumber;
	}

	public int getCountInOrder()
	{
		return countInOrder;
	}

	public int getCountOutOfOrder()
	{
		return countOutOfOrder;
	}

	public int getCountDuplicate()
	{
		return countDuplicate;
	}

	public int getCountLost()
	{
		return countLost;
	}

	public int getCountLate()
	{
		return countLate;
	}
}
