package relics;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.ThornsPower;
import com.megacrit.cardcrawl.rooms.AbstractRoom.RoomPhase;

public class BouquetWithThorns extends MyRelic{
	public static final String ID = "BouquetWithThorns";
	
	public BouquetWithThorns() {
		super(ID, RelicTier.RARE, LandingSound.CLINK);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	private void increaseThorns(int amount) {
		if (!this.isActive)
			return;
		this.addToTop(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new ThornsPower(AbstractDungeon.player, amount), amount));
	}
	
	public void atPreBattle() {
		if (!this.isActive)
			return;
		increaseThorns(1);
		this.show();
    }
	
	public int onAttacked(final DamageInfo info, final int damageAmount) {
		if (!this.isActive)
			return damageAmount;
		if (AbstractDungeon.currMapNode != null && AbstractDungeon.getCurrRoom().phase == RoomPhase.COMBAT) {
			if (info.type == DamageInfo.DamageType.NORMAL) {
				this.show();
				if (damageAmount > 0) {
					increaseThorns(2);
				} else {
					increaseThorns(1);
				}
			} else if (info.type == DamageInfo.DamageType.HP_LOSS) {
				this.show();
				increaseThorns(damageAmount);
			}
		}
		return damageAmount;
    }
	
}