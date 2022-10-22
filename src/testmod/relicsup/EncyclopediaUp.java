package testmod.relicsup;

import java.util.HashMap;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import testmod.powers.AbstractTestPower;
import testmod.relics.Encyclopedia;
import testmod.relics.Encyclopedia.EncyclopediaPower;

public class EncyclopediaUp extends AbstractUpgradedRelic {
	private static final HashMap<AbstractMonster, Integer> CURR = Encyclopedia.CURR;
	private static final HashMap<String, Integer> SEEN = Encyclopedia.SEEN;
	
	private void put(AbstractMonster m) {
		int c = (int) CURR.keySet().stream().filter(a -> a.id.equals(m.id)).count();
		CURR.put(m, c);
		int amount = SEEN.getOrDefault(m.id, 0) + c;
		if (amount > 0 && m.powers.stream().noneMatch(p -> p instanceof EncyclopediaPower)) {
			m.powers.add(new EncyclopediaPower(m, amount * 50));
		}
		if (amount > 0 && m.powers.stream().noneMatch(p -> p instanceof EncyclopediaPowerUp)) {
			m.powers.add(new EncyclopediaPowerUp(m, amount * 100));
		}
	}
	
	private void add(AbstractMonster m) {
		SEEN.computeIfPresent(m.id, (s, i) -> i + 1);
		SEEN.putIfAbsent(m.id, 1);
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		CURR.clear();
		if (AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null)
			AbstractDungeon.getMonsters().monsters.forEach(this::put);
		Encyclopedia.start = true;
	}
	
	public void update() {
		super.update();
		if (!this.isActive || !Encyclopedia.start || !this.inCombat())
			return;
		if (AbstractDungeon.getMonsters() != null && AbstractDungeon.getMonsters().monsters != null) {
			AbstractDungeon.getMonsters().monsters.stream().filter(not(CURR::containsKey)).forEach(this::put);
		}
	}
	
	public void onVictory() {
		if (!this.isActive)
			return;
		CURR.keySet().forEach(this::add);
		CURR.clear();
		//save();
		Encyclopedia.start = false;
		Encyclopedia.victory = true;
	}
	
	public static class EncyclopediaPowerUp extends AbstractTestPower {
		public EncyclopediaPowerUp(AbstractCreature owner, int amount) {
			this.owner = owner;
			this.amount = amount;
			this.type = PowerType.DEBUFF;
			this.updateDescription();
			this.addMap(p -> new EncyclopediaPowerUp(p.owner, p.amount));
		}

		public void updateDescription() {
			this.description = desc(0) + owner.name + desc(1) + (whole() ? rate() + "00" : dmgRate(100f)) + desc(2);
		}
		
		private float dmgRate(float input) {
			return chain(relicStream(EncyclopediaUp.class).map(r -> get(this::dmg))).apply(input);
		}
		
		private float dmg(float input) {
			return input * (100 + this.amount) / 100;
		}
		
		private boolean whole() {
			return this.amount % 100 == 0;
		}
		
		private static int pow(int base, int pow) {
			return pow < 1 ? 1 : (pow == 1 ? base : base * pow(base, pow - 1));
		}
		
		private int rate() {
			return pow(this.amount / 100 + 1, (int) relicStream(EncyclopediaUp.class).count()) - 1;
		}
		
		public float atDamageFinalReceive(float damage, DamageType type) {
			return type == DamageType.NORMAL ? dmgRate(damage) : damage;
		}
		
		public int onAttacked(DamageInfo info, int damage) {
			relicStream(EncyclopediaUp.class).forEach(r -> r.show());
			return damage <= 0 || info.type == DamageType.NORMAL ? damage : (int) dmgRate(damage);
		}
	}

}