package testmod.relics;

import com.evacipated.cardcrawl.mod.stslib.relics.ClickableRelic;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import testmod.potions.EscapePotion;

public class IWantAll extends AbstractTestRelic implements ClickableRelic {
	public static final int COUNT = 10;
	
	private boolean victory = false;
	
	public IWantAll() {
		super(RelicTier.SHOP, LandingSound.MAGICAL);
		this.counter = COUNT;
	}
	
	public void onVictory() {
		if (this.counter > 0)
			this.togglePulse(this, victory = true);
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		if (this.victory) {
			this.togglePulse(this, victory = false);
		}
    }
	
	private RewardItem newItem(RewardItem r) {
		RewardItem item = new RewardItem(new EscapePotion());
		item.type = RewardType.CARD;
		item.potion = null;
		item.text = RewardItem.TEXT[2];
		item.cards = r.cards.stream().map(AbstractCard::makeStatEquivalentCopy).collect(this.toArrayList());
		return item;
	}
	
	private void addReward() {
		AbstractDungeon.combatRewardScreen.rewards.addAll(AbstractDungeon.combatRewardScreen.rewards.stream()
				.filter(r -> r.type == RewardType.CARD).map(this::newItem).collect(this.toArrayList()));
		AbstractDungeon.combatRewardScreen.positionRewards();
	}
	
	private static boolean checkReward() {
		return AbstractDungeon.combatRewardScreen.rewards.stream().anyMatch(r -> r.type == RewardType.CARD);
	}
	
	@Override
	public void onRightClick() {
		if (this.victory) {
			if (!checkReward()) {
				this.togglePulse(this, victory = false);
				return;
			}
			addReward();
			this.counter--;
			if (this.counter == 0) {
				this.togglePulse(this, victory = false);
				this.counter = -2;
				this.description = this.DESCRIPTIONS[1];
				this.tips.clear();
		        this.tips.add(new PowerTip(this.name, this.description));
		        initializeTips();
			}
		}
	}
	
	public static void loadVictory() {
		MISC.relicStream(IWantAll.class).forEach(AbstractRelic::onVictory);
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}

}