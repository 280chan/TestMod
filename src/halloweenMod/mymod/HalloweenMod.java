package halloweenMod.mymod;

import java.util.ArrayList;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.ModHelper;
import com.megacrit.cardcrawl.localization.CardStrings;
import com.megacrit.cardcrawl.localization.PowerStrings;
import com.megacrit.cardcrawl.localization.RelicStrings;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rewards.RewardItem.RewardType;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.rooms.MonsterRoomBoss;
import com.megacrit.cardcrawl.rooms.MonsterRoomElite;

import basemod.BaseMod;
import basemod.interfaces.EditCardsSubscriber;
import basemod.interfaces.EditKeywordsSubscriber;
import basemod.interfaces.EditRelicsSubscriber;
import basemod.interfaces.EditStringsSubscriber;
import basemod.interfaces.PostDungeonInitializeSubscriber;
import basemod.interfaces.PostUpdateSubscriber;
import basemod.interfaces.StartGameSubscriber;
import basemod.interfaces.OnStartBattleSubscriber;
import basemod.interfaces.PostBattleSubscriber;
import halloweenMod.cards.*;
import halloweenMod.relics.*;
import halloweenMod.utils.HalloweenMiscMethods;
import testmod.mymod.TestMod;
import testmod.potions.EscapePotion;
import testmod.relicsup.EventCelebration_HalloweenUp;

/**
 * @author 彼君不触
 * @version 8/1/2020
 * @since 10/14/2018
 */

public class HalloweenMod implements EditKeywordsSubscriber, EditRelicsSubscriber, EditCardsSubscriber,
		EditStringsSubscriber, PostDungeonInitializeSubscriber, PostUpdateSubscriber, StartGameSubscriber,
		OnStartBattleSubscriber, PostBattleSubscriber, HalloweenMiscMethods {
	public static final String MOD_PREFIX = "HalloweenMod";
	
	public static void initialize() {
		TestMod.subscribeSubModClass(new HalloweenMod());
	}

	@Override
	public void receiveEditKeywords() {
		// TODO Auto-generated method stub
		switch (Settings.language) {
		case ZHS:
		case ZHT:
			BaseMod.addKeyword(new String[] { "万圣" }, "万圣是一张可以被多次升级的 #b0 费技能牌。");
			break;
		default:
			BaseMod.addKeyword(new String[] { "halloween" }, "Halloween is a #b0 cost Skill card which can be Upgraded any number of times.");
			break;
		}
	}

	@Override
	public void receiveEditRelics() {
		TestMod.addRelicsToPool(new EventCelebration_Halloween());
	}

	@Override
	public void receiveEditStrings() {
		String lang = "s_";
		switch (Settings.language) {
		case ZHS:
		case ZHT:
			lang += "zh.json";
			break;
		default:
			lang += "eng.json";
			break;
		}
		String pathPrefix = "halloweenResources/localization/" + MOD_PREFIX;
	    BaseMod.loadCustomStringsFile(RelicStrings.class, pathPrefix + "Relic" + lang);
	    BaseMod.loadCustomStringsFile(CardStrings.class, pathPrefix + "Card" + lang);
	    BaseMod.loadCustomStringsFile(PowerStrings.class, pathPrefix + "Power" + lang);
	}

	@Override
	public void receiveEditCards() {
		AbstractCard[] card = { new Candy(), new Trick(), new GhostCostume(), new Halloween() };
		for (AbstractCard c : card) {
			BaseMod.addCard(c);
			CARDS.add(c);
		}
		CARDS.remove(3);
	}
	
	public static ArrayList<AbstractCard> CARDS = new ArrayList<AbstractCard>();
	
	@Override
	public void receivePostDungeonInitialize() {
		if (AbstractDungeon.floorNum > 1) {
			return;
		}
		savedFloorNum = -2;
		changeState();
	}
	
	private static boolean check() {
		return EventCelebration_Halloween.hasThis();
	}
	
	private static boolean checkUp() {
		return EventCelebration_HalloweenUp.hasThis();
	}
	
	private boolean checkCards() {
		if (ModHelper.isModEnabled("Vintage") && (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomElite))
				&& (!(AbstractDungeon.getCurrRoom() instanceof MonsterRoomBoss))) {
			return true;
		}
		return this.getRewardCardNum() < 1;
	}
	
	public static void changeState() {
		postBattle = startGame = false;
	}
	
	private RewardItem newItem(EventCelebration_HalloweenUp ech) {
		RewardItem r = new RewardItem(new EscapePotion());
		r.type = RewardType.CARD;
		r.potion = null;
		r.text = RewardItem.TEXT[2];
		r.cards = this.getRewardCards(true);
		ech.show();
		return r;
	}
	
	@Override
	public void receivePostUpdate() {
		// TODO Auto-generated method stub
		AbstractPlayer p = AbstractDungeon.player;
		if (p != null && (postBattle || startGame) && !checkCards()) {
			if (checkUp()) {
				if (AbstractDungeon.combatRewardScreen.rewards.stream().anyMatch(r -> r.type == RewardType.CARD)) {
					changeState();
					EventCelebration_HalloweenUp.getThis().map(this::newItem)
							.forEach(AbstractDungeon.combatRewardScreen.rewards::add);
					AbstractDungeon.combatRewardScreen.positionRewards();
				}
			} else if (check()) {
				for (RewardItem r : AbstractDungeon.combatRewardScreen.rewards) {
					if (r.type == RewardType.CARD) {
						TestMod.info("万圣：更改卡牌奖励");
						r.cards = this.getRewardCards(false);
						EventCelebration_Halloween.getThis().show();
						changeState();
					}
				}
			}
		}
	}
	
	@Override
	public void receiveStartGame() {
		this.setCardRNG(AbstractDungeon.cardRng);
		if ((check() || checkUp()) && !checkCards()) {
			TestMod.info("万圣：当前楼层: " + AbstractDungeon.floorNum);
			TestMod.info("万圣：存储楼层: " + savedFloorNum);
			if (savedFloorNum == -2 && AbstractDungeon.floorNum > 0) {
				savedFloorNum = AbstractDungeon.floorNum;
			}
			if (AbstractDungeon.floorNum == savedFloorNum) {
				startGame = true;
			}
		} else {
			TestMod.info("是否持有万圣: " + check());
			TestMod.info("是否持有万圣+: " + checkUp());
		}
	}

	@Override
	public void receiveOnBattleStart(AbstractRoom room) {
		this.setRNG(AbstractDungeon.miscRng);
	}
	
	private static boolean startGame = false;
	private static boolean postBattle = false;
	public static int savedFloorNum = -2;
	
	@Override
	public void receivePostBattle(AbstractRoom room) {
		if ((check() || checkUp()) && !checkCards())
			postBattle = true;
		savedFloorNum = AbstractDungeon.floorNum;
	}

}
