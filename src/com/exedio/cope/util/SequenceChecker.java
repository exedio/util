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

import static com.exedio.cope.util.Check.requireNonNegative;

import com.exedio.cope.util.SequenceChecker2.Result;
import java.util.function.IntConsumer;

/**
 * <p><strong>Note that this class is not synchronized.</strong>
 * If multiple threads access an {@code SequenceChecker} instance concurrently,
 * it <i>must</i> be synchronized externally.
 */
public final class SequenceChecker
{
	private final SequenceChecker2 backing;

	private int countInOrder = 0;
	private int countOutOfOrder = 0;
	private int countDuplicate = 0;
	private int countLost = 0;
	private int countLate = 0;

	public SequenceChecker(final int capacity)
	{
		this.backing = new SequenceChecker2(capacity);
	}

	public int getCapacity()
	{
		return backing.getCapacity();
	}

	/**
	 * @return whether the given number is a duplicate
	 */
	public boolean check(final int number)
	{
		final Result result = backing.check(number, counterLost);
		switch(result)
		{
			case early      -> { }
			case inOrder    -> countInOrder   ++;
			case outOfOrder -> countOutOfOrder++;
			case duplicate  -> countDuplicate ++;
			case late       -> countLate      ++;
		}
		return result == Result.duplicate;
	}

	private final IntConsumer counterLost = (value) -> countLost+=value;

	public int getFirstNumber()
	{
		return backing.getFirstNumber();
	}

	public int getMaxNumber()
	{
		return backing.getMaxNumber();
	}

	public Info getInfo()
	{
		return new Info(
				countInOrder,
				countOutOfOrder,
				countDuplicate,
				countLost,
				countLate,
				backing.getPending());
	}

	// just for junit tests
	int countPending()
	{
		return backing.countPending();
	}

	public static final class Info
	{
		private final int inOrder;
		private final int outOfOrder;
		private final int duplicate;
		private final int lost;
		private final int late;
		private final int pending;

		public Info(
				final int inOrder,
				final int outOfOrder,
				final int duplicate,
				final int lost,
				final int late,
				final int pending)
		{
			this.inOrder    = requireNonNegative(inOrder,    "inOrder");
			this.outOfOrder = requireNonNegative(outOfOrder, "outOfOrder");
			this.duplicate  = requireNonNegative(duplicate,  "duplicate");
			this.lost       = requireNonNegative(lost,       "lost");
			this.late       = requireNonNegative(late,       "late");
			this.pending    = requireNonNegative(pending,    "pending");
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

		@SuppressWarnings("unused") // OK: renamed deprecated API
		public int getInOrder()
		{
			return info.getInOrder();
		}

		@SuppressWarnings("unused") // OK: renamed deprecated API
		public int getOutOfOrder()
		{
			return info.getOutOfOrder();
		}

		@SuppressWarnings("unused") // OK: renamed deprecated API
		public int getDuplicate()
		{
			return info.getDuplicate();
		}

		@SuppressWarnings("unused") // OK: renamed deprecated API
		public int getLost()
		{
			return info.getLost();
		}

		@SuppressWarnings("unused") // OK: renamed deprecated API
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
