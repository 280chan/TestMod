package halloweenMod.relics;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.relics.BlueCandle;
import com.megacrit.cardcrawl.relics.PrismaticShard;
import halloweenMod.cards.Halloween;
import halloweenMod.mymod.HalloweenMod;
import testmod.mymod.TestMod;
import testmod.relics.AbstractTestRelic;

public class EventCelebration_Halloween extends AbstractTestRelic {
	public static final String ID = HalloweenMod.MOD_PREFIX + "Halloween";
	public static final String IMG = "halloweenResources/images/relic.png";
	public static final String DESCRIPTION = "拾取时获得 #y蓝蜡烛 。战斗奖励掉落的卡牌只能从 #y糖果 、 #y捣乱 、 #y鬼妆 中选择。每当你打出 #y诅咒牌 时，将一张 #y万圣 加入手牌。";//遗物效果的文本描叙。
	
	public static boolean hasThis() {
		return AbstractDungeon.player.hasRelic(ID);
	}
	
	public static EventCelebration_Halloween getThis() {
		return (EventCelebration_Halloween) AbstractDungeon.player.getRelic(ID);
	}
	
	public EventCelebration_Halloween() {
		super(ID, IMG, RelicTier.SPECIAL, LandingSound.MAGICAL);
	}
	
	public void onPlayCard(final AbstractCard c, final AbstractMonster m) {
		if (c.type == CardType.CURSE) {
			this.atb(new MakeTempCardInHandAction(new Halloween()));
			this.show();
		}
	}
	
	public void onEquip() {
		this.addTmpEffect(() -> TestMod.obtain(p(), new BlueCandle(), false));
		AbstractDungeon.uncommonRelicPool.remove(BlueCandle.ID);
		AbstractDungeon.shopRelicPool.remove(PrismaticShard.ID);
		HalloweenMod.savedFloorNum = -2;
		HalloweenMod.changeState();
	}
	
	public void onUnequip() {
		if (!p().hasRelic(PrismaticShard.ID) && !AbstractDungeon.shopRelicPool.contains(PrismaticShard.ID))
			AbstractDungeon.shopRelicPool.add(PrismaticShard.ID);
	}
	
}