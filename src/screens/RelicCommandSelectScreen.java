package screens;

import java.util.ArrayList;
import java.util.HashMap;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import commands.RelicAdd;
import commands.RelicRemove;
import commands.TestCommand;
import mymod.TestMod;
import utils.MiscMethods;

public class RelicCommandSelectScreen extends RelicSelectScreen implements MiscMethods {
	
	private TestCommand command;
	private static HashMap<AbstractRelic, AbstractRelic> map = new HashMap<AbstractRelic, AbstractRelic>();
	private int counter;
	
	public RelicCommandSelectScreen(TestCommand command, int counter) {
		this(command);
		this.counter = counter;
	}
	
	public RelicCommandSelectScreen(TestCommand command) {
		super(true, 1, false);
		this.setDescription("选择一件遗物来执行该命令", null, null);
		this.command = command;
	}

	@Override
	protected void addRelics() {
		this.relics.addAll((command instanceof RelicAdd) ? makeCopy(TestMod.RELICS) : copyList(p().relics));
	}
	
	private ArrayList<AbstractRelic> makeCopy(ArrayList<AbstractRelic> list) {
		return list.stream().map(r -> r.makeCopy()).collect(toArrayList());
	}
	
	private ArrayList<AbstractRelic> copyList(ArrayList<AbstractRelic> list) {
		return list.stream().map(split(r -> r.makeCopy(), t())).peek(p -> map.put(p.getKey(), p.getValue()))
				.map(p -> p.getKey()).collect(toArrayList());
	}
	
	private void doSth(AbstractRelic r) {
		if (command instanceof RelicAdd)
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), r);
		else if (command instanceof RelicRemove) {
			r.onUnequip();
			p().relics.remove(r);
			p().reorganizeRelics();
		} else {
			r.counter = counter;
		}
	}
	
	@Override
	protected void afterSelected() {
		doSth(command instanceof RelicAdd ? this.selectedRelic : map.get(this.selectedRelic));
		map.clear();
	}

	@Override
	protected void afterCanceled() {
		map.clear();
	}

	@Override
	protected String categoryOf(AbstractRelic r) {
		switch(r.tier) {
		case BOSS:
			return "Boss";
		case COMMON:
			return "普通";
		case DEPRECATED:
			return "废弃";
		case RARE:
			return "稀有";
		case SHOP:
			return "商店";
		case SPECIAL:
			return "事件";
		case STARTER:
			return "初始";
		case UNCOMMON:
			return "罕见";
		}
		return "其他";
	}

	@Override
	protected String descriptionOfCategory(String category) {
		switch (category) {
		case "Boss":
			return "只在Boss宝箱中出现的遗物。";
		case "普通":
			return "很容易找到的弱小遗物。";
		case "稀有":
			return "极为少见的独特且强大的遗物。";
		case "商店":
			return "只能从商人处购买到的遗物。";
		case "事件":
			return "只能通过事件获得的遗物。";
		case "初始":
			return "角色初始携带的遗物。";
		case "罕见":
			return "比普通遗物更强大也更少见的遗物。";
		}
		return "未被分类的特殊遗物。(-1)";
	}
}