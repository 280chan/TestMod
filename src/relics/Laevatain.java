package relics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.Omamori;
import com.megacrit.cardcrawl.unlock.UnlockTracker;

import mymod.TestMod;

public class Laevatain extends MyRelic {
	public static final String ID = "Laevatain";
	public static final String IMG = TestMod.relicIMGPath(ID);
	public static final String DESCRIPTION = "拾取时获得三张随机 #y诅咒 。战斗开始时获得 #b3 点 #y力量 。每 #b3 回合获得你牌组中诅咒牌数量点 #y力量 。";//遗物效果的文本描叙。
	
	public Laevatain() {
		super(ID, new Texture(Gdx.files.internal(IMG)), RelicTier.RARE, LandingSound.HEAVY);
	}
	
	public String getUpdatedDescription() {
		return DESCRIPTIONS[0];
	}
	
	public void onEquip() {
		TestMod.setActivity(this);
		if (!isActive)
			return;
		for (AbstractCard c : Sins.SINS) {
			AbstractDungeon.curseCardPool.removeCard(c.cardID);
		}
		CardGroup group = new CardGroup(CardGroup.CardGroupType.UNSPECIFIED);
		for (int i = 0; i < 3; i++) {
			if ((AbstractDungeon.player.hasRelic("Omamori"))
					&& (AbstractDungeon.player.getRelic("Omamori").counter != 0)) {
				((Omamori) AbstractDungeon.player.getRelic("Omamori")).use();
			} else {
				AbstractCard curse = AbstractDungeon.getCard(AbstractCard.CardRarity.CURSE);
				UnlockTracker.markCardAsSeen(curse.cardID);
				group.addToBottom(curse.makeCopy());
			}
		}
	    AbstractDungeon.gridSelectScreen.openConfirmationGrid(group, "魔剑侵袭...");
    }//触发时机：当玩家获得该遗物时。(参考灵体外质、诅咒钥匙、天鹅绒项圈等)
	
	public void atPreBattle() {
		this.counter = 0;
		this.show();
		applyStrength(3);
	}
	
	private int countCurse() {
		int count = 0;
		for (AbstractCard c : AbstractDungeon.player.masterDeck.group)
			if (c.type == CardType.CURSE)
				count++;
		return count;
	}
	
	private void applyStrength(int amount) {
		AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(AbstractDungeon.player, AbstractDungeon.player, new StrengthPower(AbstractDungeon.player, amount), amount));
	}
	
	public void atTurnStart() {
		this.counter++;
		if (this.counter == 3) {
			this.counter = 0;
			if (countCurse() != 0) {
				applyStrength(countCurse());
				this.show();
			}
		}
    }//触发时机：在玩家回合开始时。
	
	public void onVictory() {
		this.counter = -1;
    }//触发时机：当玩家战斗胜利时。(参考精致折扇)
	
}