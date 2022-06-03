package testmod.screens;

import java.util.ArrayList;
import java.util.HashMap;

import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.RelicLibrary;
import com.megacrit.cardcrawl.relics.AbstractRelic;

import basemod.BaseMod;
import testmod.commands.RelicAdd;
import testmod.commands.RelicRemove;
import testmod.commands.TestCommand;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;
import testmod.relicsup.AbstractUpgradedRelic;
import testmod.utils.MiscMethods;

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
		((command instanceof RelicAdd) ? BaseMod.listAllRelicIDs().stream().filter(RelicLibrary::isARelic)
				.map(RelicLibrary::getRelic).map(r -> r.makeCopy()).collect(toArrayList()) : copyList(p().relics))
						.forEach(this.relics::add);
		if (command instanceof RelicAdd)
			TestMod.MY_RELICS.stream().map(this::upgrade).filter(r -> r != null).forEach(this.relics::add);
	}
	
	private String up(AbstractRelic r) {
		return "testmod.relicsup." + r.getClass().getSimpleName() + "Up";
	}
	
	private AbstractRelic upgrade(AbstractRelic r) {
		try {
			return (AbstractRelic) Class.forName(up(r)).newInstance();
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	private ArrayList<AbstractRelic> copyList(ArrayList<AbstractRelic> list) {
		return list.stream().map(split(r -> r.makeCopy(), t())).peek(p -> map.put(p.getKey(), p.getValue()))
				.map(p -> p.getKey()).collect(toArrayList());
	}
	
	private void doSth(AbstractRelic r) {
		if (command instanceof RelicAdd) {
			AbstractDungeon.getCurrRoom().spawnRelicAndObtain((Settings.WIDTH / 2), (Settings.HEIGHT / 2), r);
			r.isSeen = true;
		} else if (command instanceof RelicRemove) {
			r.onUnequip();
			p().relics.remove(r);
			p().reorganizeRelics();
		} else {
			r.counter = counter;
		}
	}
	
	@Override
	protected void afterSelected() {
		doSth(command instanceof RelicAdd ? this.selectedRelic.makeCopy() : map.get(this.selectedRelic));
		map.clear();
	}

	@Override
	protected void afterCanceled() {
		map.clear();
	}

	@Override
	protected String categoryOf(AbstractRelic r) {
		String postfix = r instanceof AbstractTestRelic ? "test" : "";
		if (r instanceof AbstractUpgradedRelic)
			return "升级";
		switch(r.tier) {
		case BOSS:
			return "Boss" + postfix;
		case COMMON:
			return "普通" + postfix;
		case DEPRECATED:
			return "废弃" + postfix;
		case RARE:
			return "稀有" + postfix;
		case SHOP:
			return "商店" + postfix;
		case SPECIAL:
			return "事件" + postfix;
		case STARTER:
			return "初始" + postfix;
		case UNCOMMON:
			return "罕见" + postfix;
		}
		return "未知" + postfix;
	}

	@Override
	protected String descriptionOfCategory(String category) {
		switch (category) {
		case "升级":
			return "升级后的test遗物。";
		case "Boss":
		case "Bosstest":
			return "只在Boss宝箱中出现的遗物。";
		case "普通":
		case "普通test":
			return "很容易找到的弱小遗物。";
		case "稀有":
		case "稀有test":
			return "极为少见的独特且强大的遗物。";
		case "商店":
		case "商店test":
			return "只能从商人处购买到的遗物。";
		case "事件":
		case "事件test":
			return "只能通过事件获得的遗物。";
		case "初始":
		case "初始test":
			return "角色初始携带的遗物。";
		case "罕见":
		case "罕见test":
			return "比普通遗物更强大也更少见的遗物。";
		}
		return "未知稀有度的遗物。";
	}
}