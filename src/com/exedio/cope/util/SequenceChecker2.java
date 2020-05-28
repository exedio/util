/*
 * Copyright (C) 2004-2015  exedio GmbH (www.exedio.com)
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

import static com.exedio.cope.util.Check.requireGreaterZero;

import java.util.Arrays;
import java.util.function.IntConsumer;

/**
 * <p><strong>Note that this class is not synchronized.</strong>
 * If multiple threads access an {@code SequenceChecker2} instance concurrently,
 * it <i>must</i> be synchronized externally.
 */
public final class SequenceChecker2
{
	private final int capacity;

	private final boolean[] buffer;
	private int bufferNext = 0;

	private boolean afterFirst = false;
	private int firstNumber;
	private int maxNumber;

	public enum Result
	{
		early,
		inOrder,
		outOfOrder,
		duplicate,
		late
	}
	private int pending = 0;

	public SequenceChecker2(final int capacity)
	{
		this.capacity = requireGreaterZero(capacity, "capacity");
		buffer = new boolean[capacity];
		Arrays.fill(buffer, true);
		//System.out.println("----------------");
	}

	public int getCapacity()
	{
		return capacity;
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
	public Result check(final int number, final IntConsumer lost)
	{
		//System.out.println("-----------" + number + "----" + toStringInternal());
		if(afterFirst)
		{
			if(number>maxNumber)
			{
				//System.out.println("-----------" + number + "----countInOrder");
				final int todo = number - maxNumber;
				int lostUpdate = 0;
				for(int i = 0; i<todo; i++)
				{
					if(!buffer[bufferNext])
						lostUpdate++;
					buffer[bufferNext] = (i==(todo-1));

					bufferNext++;
					if(bufferNext>=capacity)
						bufferNext = 0;
				}
				maxNumber = number;
				pending += todo-1-lostUpdate;
				if(lostUpdate!=0)
					lost.accept(lostUpdate);

				return Result.inOrder;
			}
			else if(number<firstNumber)
			{
				//System.out.println("-----------" + number + "----ignore");
				// forget, since I don't know anything about numbers before firstNumber
				return Result.early;
			}
			else if(number>(maxNumber-capacity))
			{
				int pos = bufferNext - (maxNumber-number) - 1;
				if(pos<0)
					pos += capacity;

				//System.out.println("-----------" + number + "----outOfOrder or duplicate ---- on " + pos);
				if(buffer[pos])
				{
					//System.out.println("-----------" + number + "----duplicate");
					return Result.duplicate;
				}
				else
				{
					//System.out.println("-----------" + number + "----outOfOrder");
					buffer[pos] = true;
					pending--;

					return Result.outOfOrder;
				}
			}
			else
			{
				return Result.late;
			}
		}
		else
		{
			afterFirst = true;
			firstNumber = number;
			maxNumber = number;

			return Result.inOrder;
		}
	}

	public int getFirstNumber()
	{
		assertAfterFirst();
		return firstNumber;
	}

	public int getMaxNumber()
	{
		assertAfterFirst();
		return maxNumber;
	}

	private void assertAfterFirst()
	{
		if(!afterFirst)
			throw new IllegalStateException("did not yet check first number");
	}

	public int getPending()
	{
		return pending;
	}

	// just for junit tests
	int countPending()
	{
		int result = 0;
		for(final boolean b : buffer)
			if(!b)
				result++;
		return result;
	}
}
