package relics;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import potions.EscapePotion;

public class IWantAll extends AbstractClickRelic {
	public static final String ID = "IWantAll";
	public static final int COUNT = 10;
	
	private boolean victory = false;
	
	public IWantAll() {
		super(ID, RelicTier.SHOP, LandingSound.MAGICAL);
		this.counter = COUNT;
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}

	public void updateDescription(PlayerClass c) {
		this.tips.clear();
	    this.tips.add(new PowerTip(this.name, this.getUpdatedDescription()));
	    initializeTips();
	}
	
	private void toggleState(boolean victory) {
		Lambda a = ((this.victory = victory) ? this::beginLongPulse : this::stopPulse);
		a.act();
	}
	
	public void onVictory() {
		if (this.counter > 0)
			this.toggleState(true);
    }
	
	public void onEnterRoom(final AbstractRoom room) {
		if (this.victory) {
			this.toggleState(false);
		}
    }
	
	private RewardItem newItem(RewardItem r) {
		RewardItem item = new RewardItem(new EscapePotion());
		item.type = RewardType.CARD;
		item.potion = null;
		item.text = RewardItem.TEXT[2];
		item.cards = r.cards.stream().map(AbstractCard::makeStatEquivalentCopy).collect(this.collectToArrayList());
		return item;
	}
	
	private void addReward() {
		AbstractDungeon.combatRewardScreen.rewards.addAll(AbstractDungeon.combatRewardScreen.rewards.stream()
				.filter(r -> r.type == RewardType.CARD).map(this::newItem).collect(this.collectToArrayList()));
		AbstractDungeon.combatRewardScreen.positionRewards();
	}
	
	private static boolean checkReward() {
		return AbstractDungeon.combatRewardScreen.rewards.stream().anyMatch(r -> r.type == RewardType.CARD);
	}
	
	@Override
	protected void onRightClick() {
		if (this.victory) {
			if (!checkReward()) {
				this.toggleState(false);
				return;
			}
			addReward();
			this.counter--;
			if (this.counter == 0) {
				this.toggleState(false);
				this.counter = -2;
				this.description = this.DESCRIPTIONS[1];
				this.tips.clear();
		        this.tips.add(new PowerTip(this.name, this.description));
		        initializeTips();
			}
		}
	}
	
	public static void loadVictory() {
		AbstractDungeon.player.relics.stream().filter(r -> r instanceof IWantAll).forEach(AbstractRelic::onVictory);
	}
	
	public boolean canSpawn() {
		return Settings.isEndless || AbstractDungeon.actNum < 3;
	}

}