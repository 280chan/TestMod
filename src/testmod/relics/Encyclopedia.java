package testmod.relics;

import java.util.ArrayList;
import java.util.HashMap;

import com.evacipated.cardcrawl.modthespire.lib.LineFinder;
import com.evacipated.cardcrawl.modthespire.lib.Matcher;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertLocator;
import com.evacipated.cardcrawl.modthespire.lib.SpireInsertPatch;
import com.evacipated.cardcrawl.modthespire.lib.SpirePatch;
import com.evacipated.cardcrawl.modthespire.patcher.PatchingException;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.saveAndContinue.SaveAndContinue;

import javassist.CannotCompileException;
import javassist.CtBehavior;
import testmod.mymod.TestMod;
import testmod.powers.AbstractTestPower;
import testmod.relicsup.EncyclopediaUp;

public class Encyclopedia extends AbstractTestRelic {
	public static final String SAVE_NAME = "Encyclopedia";
	public static final HashMap<AbstractMonster, Integer> CURR = new HashMap<AbstractMonster, Integer>();
	public static final HashMap<String, Integer> SEEN = new HashMap<String, Integer>();
	public static boolean start = false;
	public static boolean victory = false;
	
	public static void clear() {
		SEEN.clear();
		save();
	}
	
	public static void load() {
		SEEN.clear();
		int size = TestMod.getInt(SAVE_NAME);
		for (int i = 1; i <= size; i++)
			SEEN.put(TestMod.getString(SAVE_NAME + (-i)), TestMod.getInt(SAVE_NAME + i));
		victory = start = false;
	}
	
	private static void save() {
		victory = false;
		int size;
		TestMod.save(SAVE_NAME, size = SEEN.size());
		ArrayList<String> id = SEEN.keySet().stream().collect(MISC.toArrayList());
		for (int i = 1; i <= size; i++) {
			TestMod.save(SAVE_NAME + (-i), id.get(i - 1));
			TestMod.save(SAVE_NAME + i, SEEN.get(id.get(i - 1)));
		}
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (this.isActive) {
			clear();
			if (this.inCombat() && !start) {
				this.atPreBattle();
			}
		}
	}
	
	private void put(AbstractMonster m) {
		int c = (int) CURR.keySet().stream().filter(a -> a.id.equals(m.id)).count();
		CURR.put(m, c);
		int amount = SEEN.getOrDefault(m.id, 0) + c;
		if (amount > 0 && m.powers.stream().noneMatch(p -> p instanceof EncyclopediaPower)) {
			m.powers.add(new EncyclopediaPower(m, amount * 50));
		}
	}
	
	private void add(AbstractMonster m) {
		SEEN.computeIfPresent(m.id, (s, i) -> i + 1);
		SEEN.putIfAbsent(m.id, 1);
	}
	
	public void atPreBattle() {
		if (!this.isActive || this.relicStream(EncyclopediaUp.class).count() > 0)
			return;
		CURR.clear();
		if (AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null)
			AbstractDungeon.getMonsters().monsters.forEach(this::put);
		start = true;
	}
	
	public void update() {
		super.update();
		if (!this.isActive || !start || !this.inCombat() || this.relicStream(EncyclopediaUp.class).count() > 0)
			return;
		if (AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null) {
			AbstractDungeon.getMonsters().monsters.stream().filter(not(CURR::containsKey)).forEach(this::put);
		}
	}
	
	public void onVictory() {
		if (!this.isActive || this.relicStream(EncyclopediaUp.class).count() > 0)
			return;
		CURR.keySet().forEach(this::add);
		CURR.clear();
		//save();
		start = false;
		victory = true;
	}
	
	@SpirePatch(clz = AbstractRoom.class, method = "update")
	public static class StupidSavePatch {
		@SpireInsertPatch(locator = Locator.class)
		public static void Insert(AbstractRoom room) {
			if (victory)
				save();
		}
	}
	
	private static class Locator extends SpireInsertLocator {
		public int[] Locate(CtBehavior ctMethodToPatch) throws CannotCompileException, PatchingException {
			Matcher finalMatcher = new Matcher.MethodCallMatcher(SaveAndContinue.class, "save");
			int[] raw = LineFinder.findAllInOrder(ctMethodToPatch, new ArrayList<Matcher>(), finalMatcher);
			return new int[] { raw[0] + 1 };
		}
	}
	
	public static class EncyclopediaPower extends AbstractTestPower {
		public EncyclopediaPower(AbstractCreature owner, int amount) {
			this.owner = owner;
			this.amount = amount;
			this.type = PowerType.DEBUFF;
			this.updateDescription();
			this.addMap(p -> new EncyclopediaPower(p.owner, p.amount));
		}

		public void updateDescription() {
			this.description = desc(0) + owner.name + desc(1) + (single() ? amount + "" : (dmgRate(100f) - 100))
					+ desc(2);
		}
		
		private float dmgRate(float input) {
			return chain(relicStream(Encyclopedia.class).map(r -> get(this::dmg))).apply(input);
		}
		
		private float dmg(float input) {
			return input * (100 + this.amount) / 100;
		}

		private boolean single() {
			return relicStream(Encyclopedia.class).count() == 1;
		}
		
		public float atDamageFinalReceive(float damage, DamageType type) {
			return type == DamageType.NORMAL ? dmgRate(damage) : damage;
		}
		
		public int onAttacked(DamageInfo info, int damage) {
			relicStream(Encyclopedia.class).forEach(r -> r.show());
			return damage <= 0 || info.type == DamageType.NORMAL ? damage : (int) dmgRate(damage);
		}
		
	}

}