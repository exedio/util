/*
 * Copyright (C) 2004-2009  exedio GmbH (www.exedio.com)
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
	private final int capacity;

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

	public SequenceChecker(final int capacity)
	{
		if(capacity<1)
			throw new IllegalArgumentException("capacity must be greater than zero, but was " + capacity);

		this.capacity = capacity;
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
	public boolean check(final int number)
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
					if(bufferNext>=capacity)
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
			else if(number>(maxNumber-capacity))
			{
				int pos = bufferNext - (maxNumber-number) - 1;
				if(pos<0)
					pos += capacity;

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

	public Info getInfo()
	{
		int countPending = 0;
		for(final boolean b : buffer)
			if(!b)
				countPending++;

		return new Info(
				countInOrder,
				countOutOfOrder,
				countDuplicate,
				countLost,
				countLate,
				countPending);
	}

	public static final class Info
	{
		private final int inOrder;
		private final int outOfOrder;
		private final int duplicate;
		private final int lost;
		private final int late;
		private final int pending;

		Info(
				final int inOrder,
				final int outOfOrder,
				final int duplicate,
				final int lost,
				final int late,
				final int pending)
		{
			this.inOrder    = inOrder;
			this.outOfOrder = outOfOrder;
			this.duplicate  = duplicate;
			this.lost       = lost;
			this.late       = late;
			this.pending    = pending;
		}

		public int getInOrder()
		{
			return inOrder;
		}

		public int getOutOfOrder()
		{
			return outOfOrder;
		}

		public int getDuplicate()
		{
			return duplicate;
		}

		public int getLost()
		{
			return lost;
		}

		public int getLate()
		{
			return late;
		}

		public int getPending()
		{
			return pending;
		}
	}

	// ------------------- deprecated stuff -------------------

	/**
	 * @deprecated Use {@link #getInfo()} instead.
	 */
	@Deprecated
	public Counter getCounter()
	{
		return new Counter(getInfo());
	}

	/**
	 * @deprecated Use {@link Info} instead.
	 */
	@Deprecated
	public static final class Counter
	{
		private final Info info;

		Counter(final Info info)
		{
			this.info = info;
		}

		public int getInOrder()
		{
			return info.getInOrder();
		}

		public int getOutOfOrder()
		{
			return info.getOutOfOrder();
		}

		public int getDuplicate()
		{
			return info.getDuplicate();
		}

		public int getLost()
		{
			return info.getLost();
		}

		public int getLate()
		{
			return info.getLate();
		}
	}

	/**
	 * @deprecated Use {@link Info#getInOrder()} instead.
	 */
	@Deprecated
	public int getCountInOrder()
	{
		return countInOrder;
	}

	/**
	 * @deprecated Use {@link Info#getOutOfOrder()} instead.
	 */
	@Deprecated
	public int getCountOutOfOrder()
	{
		return countOutOfOrder;
	}

	/**
	 * @deprecated Use {@link Info#getDuplicate()} instead.
	 */
	@Deprecated
	public int getCountDuplicate()
	{
		return countDuplicate;
	}

	/**
	 * @deprecated Use {@link Info#getLost()} instead.
	 */
	@Deprecated
	public int getCountLost()
	{
		return countLost;
	}

	/**
	 * @deprecated Use {@link Info#getLate()} instead.
	 */
	@Deprecated
	public int getCountLate()
	{
		return countLate;
	}

	/**
	 * @deprecated Use {@link #getCapacity()} instead
	 */
	@Deprecated
	public int getLength()
	{
		return getCapacity();
	}
}
