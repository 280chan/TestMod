package relics;

import java.util.ArrayList;
import java.util.HashMap;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.AbstractPower;
import powers.AbstractTestPower;

public class GreedyDevil extends AbstractTestRelic {
	private final HashMap<DamageInfo, AbstractCreature> MAP = new HashMap<DamageInfo, AbstractCreature>();
	private final ArrayList<AbstractPower> TO_REMOVE = new ArrayList<AbstractPower>();
	
	public GreedyDevil() {
		super(RelicTier.RARE, LandingSound.MAGICAL);
	}
	
	public void onAttack(final DamageInfo info, final int damageAmount, final AbstractCreature target) {
		if (damageAmount > 0 && !target.isPlayer) {
			this.MAP.put(info, target);
			AbstractTestPower p = new AbstractTestPower(this.relicId) {
			    public int onAttacked(final DamageInfo info, final int damage) {
			    	if (MAP.containsKey(info)) {
						int dmg = damage - target.currentHealth;
						if (dmg > 0) {
							AbstractDungeon.player.gainGold(Math.min(dmg, 100));
							GreedyDevil.this.flash();
						}
						MAP.remove(info);
						TO_REMOVE.add(this);
					}
					return damage;
			    }
			};
			p.owner = target;
			target.powers.add(p);
		}
    }
	
	public void onVictory() {
		this.MAP.clear();
		this.TO_REMOVE.clear();
	}
	
	public void update() {
		super.update();
		if (this.TO_REMOVE.isEmpty())
			return;
		this.TO_REMOVE.stream().forEach(p -> p.owner.powers.remove(p));
		this.TO_REMOVE.clear();
	}

}