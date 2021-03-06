package io.ffreedom.common.queue.disruptor;

import java.util.concurrent.TimeUnit;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.BusySpinWaitStrategy;
import com.lmax.disruptor.LiteBlockingWaitStrategy;
import com.lmax.disruptor.LiteTimeoutBlockingWaitStrategy;
import com.lmax.disruptor.SleepingWaitStrategy;
import com.lmax.disruptor.TimeoutBlockingWaitStrategy;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.YieldingWaitStrategy;

public final class WaitStrategyFactory {

	private WaitStrategyFactory() {
	}

	public static WaitStrategy newWaitStrategy(WaitStrategyOption option) {
		switch (option) {
		case BusySpin:
			return new BusySpinWaitStrategy();
		case Blocking:
			return new BlockingWaitStrategy();
		case LiteBlocking:
			return new LiteBlockingWaitStrategy();
		case TimeoutBlocking:
			return new TimeoutBlockingWaitStrategy(1, TimeUnit.MICROSECONDS);
		case LiteTimeoutBlocking:
			return new LiteTimeoutBlockingWaitStrategy(1, TimeUnit.MICROSECONDS);
		case PhasedBackoff:
			throw new RuntimeException("PhasedBackoffWaitStrategy does not support.");
		case Sleeping:
			return new SleepingWaitStrategy();
		case Yielding:
			return new YieldingWaitStrategy();
		default:
			break;
		}
		return null;
	}

}
