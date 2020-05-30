package relics;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer.PlayerClass;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.PowerTip;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;

import mymod.TestMod;
import potions.EscapePotion;

public class IWantAll extends AbstractClickRelic {
	public static final String ID = "IWantAll";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "有 #b10 次机会，可以在战斗奖励界面右键点击此遗物，添加一份完全相同的该次战斗的卡牌奖励，每场战斗只能使用一次。";//遗物效果的文本描叙。
	public static final int COUNT = 10;
	
	private boolean victory = false;
	
	public IWantAll() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.SHOP, LandingSound.MAGICAL);
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
		this.victory = victory;
		if (victory) {
			this.beginLongPulse();
		} else {
			this.stopPulse();
		}
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
	
	private void addReward() {
		ArrayList<RewardItem> bonus = new ArrayList<RewardItem>();
		for (RewardItem r : AbstractDungeon.combatRewardScreen.rewards) {
			if (r.type == RewardType.CARD) {
				RewardItem item = new RewardItem(new EscapePotion());
				item.type = RewardType.CARD;
				item.potion = null;
				item.text = RewardItem.TEXT[2];
				item.cards = new ArrayList<AbstractCard>();
				for (AbstractCard c : r.cards) {
					item.cards.add(c.makeStatEquivalentCopy());
				}
				bonus.add(item);
			}
		}
		for (RewardItem r : bonus) {
			AbstractDungeon.combatRewardScreen.rewards.add(r);
		}
		AbstractDungeon.combatRewardScreen.positionRewards();
	}
	
	@Override
	protected void onRightClick() {
		if (this.victory) {
			this.toggleState(false);
			this.addReward();
			this.counter--;
			if (this.counter == 0) {
				this.counter = -2;
				this.description = this.DESCRIPTIONS[1];
				this.tips.clear();
		        this.tips.add(new PowerTip(this.name, this.description));
		        initializeTips();
			}
		}
	}
	
	public boolean canSpawn() {
		if (!Settings.isEndless && AbstractDungeon.actNum > 1) {
			return false;
		}
		return true;
	}

}