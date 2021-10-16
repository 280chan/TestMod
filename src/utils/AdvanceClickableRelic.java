package utils;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.megacrit.cardcrawl.core.OverlayMenu;
import com.megacrit.cardcrawl.helpers.input.InputHelper;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Stream;
import javassist.CtBehavior;
import org.apache.commons.lang3.NotImplementedException;
import utils.MiscMethods.Lambda;

@SuppressWarnings({ "rawtypes", "unchecked" })
public interface AdvanceClickableRelic<T extends AdvanceClickableRelic<T>> {
	static HashMap<AdvanceClickableRelic, ClickManager> MAP = new HashMap<AdvanceClickableRelic, ClickManager>();
	
	/**
	 * @param duration	Duration to set
	 * 					想要设置的时长
	 * @return 			Same relic
	 * 					设置了时长后的同一遗物
	 */
	default T setDuration(int duration) {
		manager(this).DURATION = duration;
		return (T) this;
	}
	
	/**
	 * Add actions to do in order during the duration
	 * 在期间内右键遗物依次触发的效果，第一次触发第一个，第二次触发第二个
	 * @param actions
	 * @return	Same relic
	 * 			添加了右键效果后的同一遗物
	 */
	default T addRightClickActions(Lambda... actions) {
		if (manager(this).actions == null)
			manager(this).actions = new ArrayList<Lambda>();
		Stream.of(actions).forEachOrdered(manager(this).actions::add);
		return (T) this;
	}
	
	/**
	 * Add selection choices to do depends on total click times during the duration
	 * 在期间内右键遗物，期间结束后依据点击次数决定触发哪一个效果，一次触发第一个，二次触发第二个
	 * @param actions
	 * @return	Same relic
	 * 			添加了右键效果后的同一遗物
	 */
	default T addRightClickSelections(Lambda... actions) {
		if (manager(this).selections == null)
			manager(this).selections = new ArrayList<Lambda>();
		Stream.of(actions).forEachOrdered(manager(this).selections::add);
		return (T) this;
	}
	
	/**
	 * @return	current click times during the duration
	 * 			当前期间的点击次数
	 */
	default int clickTimes() {
		return manager(this).clickTimes;
	}
	
	/**
	 * Basic function, called each time right click on relic
	 * 基本的效果，每次右键遗物均会触发
	 */
	void onEachRightClick();
	
	/**
	 * called after duration ends, if only 1 right click on the relic during the duration
	 * 在给定时间段内仅仅右键遗物了一次后触发
	 */
	void onSingleRightClick();
	
	/**
	 * called when duration ends
	 * 在右键遗物后给定时间段结束后触发
	 */
	void onDurationEnd();
	
	class ClickManager<R extends AdvanceClickableRelic<R>> {
		private boolean RclickStart, Rclick, dCheck, reseted;
		private long lastClick;
		private int clickTimes, DURATION = 300;
		private ArrayList<Lambda> actions, selections;
		private AdvanceClickableRelic<R> r;
		ClickManager(AdvanceClickableRelic<R> r) {
			this.r = r;
		}
		private boolean checkSingleClick() {
			return deltaTime() >= DURATION && dCheck && clickTimes == 1;
		}
		private long deltaTime() {
			return System.currentTimeMillis() - this.lastClick;
		}
		private boolean checkMultiClick() {
			return this.deltaTime() < DURATION;
		}
		private void updateClickTime() {
			this.reseted = false;
			this.lastClick = System.currentTimeMillis();
		}
		private void checkReset() {
			if (deltaTime() >= DURATION && !reseted) {
				reseted = true;
				System.out.print("右击选择触发，共" + clickTimes + "次，执行第" + clickTimes + "个");
				tryRun(selections);
				r.onDurationEnd();
				this.clickTimes = 0;
			}
		}
		private void runCurrentAction() {
			System.out.print("右击依次触发的第" + clickTimes + "次");
			tryRun(actions);
		}
		private void tryRun(ArrayList<Lambda> list) {
			if (list == null || this.clickTimes > list.size()) {
				System.out.println(", 但执行条件不满足，未执行");
				return;
			}
			Runnable tmp = list.get(this.clickTimes - 1);
			if (tmp != null) {
				tmp.run();
				System.out.println(", 执行成功");
			} else {
				System.out.println(", 但目标为null，无法执行");
			}
		}
		private void clickUpdate() {
			if (this.r instanceof AbstractRelic) {
				AbstractRelic r = (AbstractRelic) this.r;
				if (RclickStart && InputHelper.justReleasedClickRight) {
					if (r.hb.hovered) {
						Rclick = true;
						System.out.print("右键点击了" + r.name + ", 本次右击是初次右击");
						if (clickTimes++ == 0) {
							updateClickTime();
							System.out.println("，开始计时");
						} else {
							System.out.println("开始" + DURATION + "毫秒内第" + clickTimes + "次右击");
						}
						this.r.onEachRightClick();
					}
					RclickStart = false;
				}
				if (r.hb != null && r.hb.hovered && InputHelper.justClickedRight) {
					RclickStart = true;
				}
				if (checkSingleClick()) {
					dCheck = false;
					System.out.println(DURATION + "毫秒内只点击了一次");
					this.r.onSingleRightClick();
				}
				if (Rclick) {
					Rclick = false;
					dCheck = true;
					if (checkMultiClick()) {
						runCurrentAction();
					}
				}
				checkReset();
			} else {
				throw new NotImplementedException("BetterClickableRelic interface implemented by non-relic class");
			}
		}
	}
	
	static <R extends AdvanceClickableRelic<R>> ClickManager<R> manager(AdvanceClickableRelic<R> r) {
		if (!MAP.containsKey(r)) {
			MAP.put(r, new ClickManager<R>(r));
		}
		return MAP.get(r);
	}

	default boolean hovered() {
		if (this instanceof AbstractRelic) {
			AbstractRelic relic = (AbstractRelic) this;
			return relic.hb.hovered;
		} else {
			throw new NotImplementedException("BetterClickableRelic interface implemented by non-relic class");
		}
	}

	@SpirePatch(clz = OverlayMenu.class, method = "update")
	public static class ClickableRelicUpdatePatch {
		@SpireInsertPatch(locator = Locator.class, localvars = { "r" })
		public static void Insert(OverlayMenu __instance, AbstractRelic r) {
			if (r instanceof AdvanceClickableRelic) {
				manager((AdvanceClickableRelic) r).clickUpdate();
			}
		}

		private static class Locator extends SpireInsertLocator {
			public int[] Locate(CtBehavior ctMethodToPatch) throws Exception {
				Matcher.MethodCallMatcher methodCallMatcher = new Matcher.MethodCallMatcher(AbstractRelic.class,
						"update");
				return LineFinder.findInOrder(ctMethodToPatch, methodCallMatcher);
			}
		}
	}
}

