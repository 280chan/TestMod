package relics;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import powers.HolyLightProtectionPower;

public class HolyLightProtection extends AbstractTestRelic {
	private ArrayList<AbstractMonster> list = new ArrayList<AbstractMonster>();
	private boolean checkPulse = false;
	
	public HolyLightProtection() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
		this.counter = -1;
	}
	
	private static MonsterGroup m() {
		return AbstractDungeon.getMonsters();
	}
	
	private void f(AbstractMonster m) {
		checkPulse = true;
	}
	
	private void addPower() {
		if (this.isActive && this.counter == -3) {
			checkPulse = false;
			m().monsters.stream().filter(m -> !m.isDeadOrEscaped()).filter(not(list::contains))
					.filter(HolyLightProtectionPower::notHasThis).peek(this::f)
					.forEach(m -> m.powers.add(new HolyLightProtectionPower(m)));
			if (checkPulse)
				this.beginLongPulse();
		}
	}
	
	public void atPreBattle() {
		if (this.isActive && m() != null && m().monsters != null) {
			list.clear();
			m().monsters.stream().limit(2).forEach(list::add);
			this.counter = list.size() == 2 ? -3 : -2;
			addPower();
		}
    }
	
	public void update() {
		super.update();
		if (this.isActive && this.counter == -2) {
			m().monsters.stream().filter(not(list::contains)).limit(2 - list.size()).forEach(list::add);
			this.counter = list.size() == 2 ? -3 : -2;
		}
		addPower();
	}
	
	public void onVictory() {
		this.counter = -1;
		this.list.clear();
		this.stopPulse();
		this.checkPulse = false;
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
        return (info.owner == null || info.owner.isPlayer || list.contains(info.owner)) ? damageAmount : 0;
    }
	
	public void onMonsterDeath(AbstractMonster m) {
		if (!list.contains(m)
				&& m().monsters.stream().filter(not(list::contains)).noneMatch(c -> !c.isDeadOrEscaped())) {
			this.stopPulse();
		}
	}

}