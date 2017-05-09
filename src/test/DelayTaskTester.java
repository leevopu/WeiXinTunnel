package test;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

import com.weixin.corp.entity.message.RequestCall;
import com.weixin.corp.utils.CommonUtil;

public class DelayTaskTester {

	private static DelayQueue<DelayTask> delayMessageQueue = new DelayQueue<DelayTask>();

	public static void main(String args[]) {

		// ExecutorService executorService = Executors.newFixedThreadPool(2,
		// new ThreadFactory() {
		//
		// @Override
		// public Thread newThread(Runnable r) {
		// Thread t = new Thread(r);
		// t.setDaemon(true);
		// return t;
		// }
		// });

		// executorService.submit(DelayTaskTester.new DelayTaskCustomer());
		// executorService.submit(DelayTaskTester.new DelayTaskCustomer());

		Random random = new Random();

		long startTime = System.currentTimeMillis();

		for (int i = 0; i < 100; i++) {
			DelayTask x = new DelayTask(random.nextInt(20 * 1000 * 60),
					startTime);
			DelayTaskTester.delayMessageQueue.offer(x);
		}
		long minTime = Long.MAX_VALUE;
		long maxTime = 0;
		Iterator<DelayTask> iterator = DelayTaskTester.delayMessageQueue.iterator();
		while (iterator.hasNext()) {
			DelayTask next = iterator.next();
			if (minTime > next.getExpectTime()) {
				minTime = next.getExpectTime();
			}
			if(maxTime < next.getExpectTime()){
				maxTime = next.getExpectTime();
			}
		}
		System.out.println(CommonUtil.getDateStr(new Date(minTime),
				"yyyy-MM-dd HH:mm:ss SSS"));
		System.out.println(CommonUtil.getDateStr(new Date(maxTime),
				"yyyy-MM-dd HH:mm:ss SSS"));
		DelayTask delayTask;
		int addCount = 1;
		try {
			while (true) {
				delayTask = DelayTaskTester.delayMessageQueue.take();
				System.out.println(CommonUtil.getDateStr(
						new Date(delayTask.getExpectTime()),
						"yyyy-MM-dd HH:mm:ss SSS"));
				System.out.println(123);
				if(98 == DelayTaskTester.delayMessageQueue.size() && addCount == 1){
					String addOne = "2017-05-03 17:52:36";
					long delayTime = CommonUtil.getStrDate(addOne, "yyyy-MM-dd HH:mm:ss").getTime() - System.currentTimeMillis();
					DelayTask x = new DelayTask(delayTime,
							System.currentTimeMillis());
					System.out.println(DelayTaskTester.delayMessageQueue.size());
					DelayTaskTester.delayMessageQueue.offer(x);
					addCount--;
					System.out.println(DelayTaskTester.delayMessageQueue.size());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		// executorService.shutdown();

	}

	class DelayTaskCustomer implements Runnable {

		@Override
		public void run() {

			while (true) {
				try {
					DelayTask delayTask = delayMessageQueue.take();

					delayTask.print();

				} catch (Throwable e) {
					e.printStackTrace();
				}
			}

		}

	}
}

class DelayTask implements Delayed {

	//
	private final long timeStamp;

	// 记录开始时间
	private final long startTime;

	private final long delayTime;
	
	private RequestCall call;

	public DelayTask(final long delayTime, final long startTime) {
		this.delayTime = delayTime;
		this.timeStamp = System.currentTimeMillis();

		this.startTime = startTime;
	}

	public long getExpectTime() {
		return timeStamp + delayTime;
	}

	@Override
	public int compareTo(Delayed o) {
		if (this.getExpectTime() > ((DelayTask) o).getExpectTime()) {
			return 1;
		} else if (this.getExpectTime() < ((DelayTask) o).getExpectTime()) {
			return -1;
		}

		return 0;
	}

	@Override
	public long getDelay(TimeUnit unit) {

		return unit.convert(
				(this.timeStamp + delayTime) - System.currentTimeMillis(),
				TimeUnit.MILLISECONDS);
	}

	/*
 */
	public void print() {
		long now = System.currentTimeMillis();
		long realDelayTime = now - this.timeStamp;
		long Deviation = realDelayTime - this.delayTime;

		System.out.println(Thread.currentThread().getName() + "--延迟时间是:"
				+ this.delayTime + "..真实延迟时间.......:" + realDelayTime
				+ "......误差时间(单位毫秒)..::" + Deviation + "此时完成任务时间共经历时间: "
				+ (now - startTime));
	}
}