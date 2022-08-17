package testmod.relicsup;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PlatedArmorPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import testmod.relics.PortableAltar;

public class PortableAltarUp extends AbstractUpgradedRelic implements ClickableRelic {
	
	private void updateMaxHPLost(int preMaxHP, int toLose) {
		int actual = p().maxHealth - preMaxHP;
		if (preMaxHP <= toLose) {
			toLose = preMaxHP - 1;
		}
		toLose = Math.max(toLose, actual);
		this.counter += toLose;
	}
	
	public void loseMaxHP(int toLose) {
		int tempMaxHP = p().maxHealth;
		p().decreaseMaxHealth(toLose);
		updateMaxHPLost(tempMaxHP, toLose);
	}
	
	public void onEquip() {
		this.counter = Math.max(0, PortableAltar.maxHPLost);
	}
	
	public void onUnequip() {
		if (!isActive)
			return;
		p().increaseMaxHp(this.counter, true);
    }
	
	public void atBattleStart() {
		if (!isActive)
			return;
		if (counter > 1) {
			this.show();
			this.att(apply(p(), new StrengthPower(p(), counter / 2)));
			this.att(apply(p(), new DexterityPower(p(), counter / 2)));
			this.att(apply(p(), new PlatedArmorPower(p(), counter / 2)));
		}
	}
	
	public float preChangeMaxHP(float amount) {
		return amount >= 0 ? (1 + counter / 20f) * amount : amount;
	}
	
	public void onEnterRoom(final AbstractRoom room) {
		if (!isActive)
			return;
		this.show();
		this.loseMaxHP(1);
    }

	@Override
	public void onRightClick() {
		if (this.inCombat()) {
			this.show();
			this.loseMaxHP(1);
		} else if (this.counter > 1) {
			int tmp = this.counter / 2;
			this.counter -= tmp;
			this.addRandomKey();
			this.show();
			p().increaseMaxHp(tmp, true);
		}
	}
	
}