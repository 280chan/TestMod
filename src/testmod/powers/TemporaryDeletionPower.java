package testmod.powers;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;

import com.megacrit.cardcrawl.actions.common.MakeTempCardInHandAction;
import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.cards.AbstractCard.CardColor;
import com.megacrit.cardcrawl.cards.AbstractCard.CardRarity;
import com.megacrit.cardcrawl.cards.AbstractCard.CardType;
import com.megacrit.cardcrawl.cards.CardGroup;
import com.megacrit.cardcrawl.cards.CardGroup.CardGroupType;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.helpers.CardLibrary;
import com.megacrit.cardcrawl.localization.PowerStrings;
import basemod.BaseMod;
import testmod.cards.AbstractTestCurseCard;
import testmod.cards.mahjong.AbstractMahjongCard;
import testmod.mymod.TestMod;

public class TemporaryDeletionPower extends AbstractTestPower {
	public static final String POWER_ID = "TemporaryDeletionPower";
	private static final PowerStrings PS = Strings(POWER_ID);
	private static final String NAME = PS.NAME;
	private static final String[] DESCRIPTIONS = PS.DESCRIPTIONS;
	private static final String[] RARITY = split(3, 9);
	private CardRarity rarity;
	private CardGroup group;
	private int index = 0;
	private boolean fuckedRarity;
	
	private static String[] split(int start, int end) {
		String[] tmp = new String[end - start];
		for (int i = 0; i < tmp.length; i++)
			tmp[i] = DESCRIPTIONS[start + i];
		return tmp;
	}
	
	public TemporaryDeletionPower(AbstractCreature owner, int amount, AbstractCard c) {
		super(POWER_ID);
		this.owner = owner;
		this.amount = amount;

		this.fuckedRarity = c.getClass().getSuperclass().getCanonicalName().equals("lobotomyMod.card.AbstractLobotomyCard");
		
		ArrayList<AbstractCard> list = getList(c.rarity, c.color);
		
		// ??? WTF did I write
		if (c.rarity != CardRarity.BASIC && c.rarity != CardRarity.SPECIAL && c.color == CardColor.COLORLESS && c.type != CardType.STATUS) {
			list.addAll(getList(c.rarity, c.color));
		}
		
		this.group = getGroup(list);
		this.index = c.rarity.ordinal();
		this.rarity = c.rarity;
		this.name = NAME + this.rarityDesc();
		this.ID += "" + c.color + this.index;
		updateDescription();
		this.type = PowerType.BUFF;
	}
	
	private String rarityDesc() {
		return "[" + (this.index < RARITY.length ? RARITY[index] : this.rarity.toString()) + "]";
	}
	
	public void updateDescription() {
		 this.description = DESCRIPTIONS[0] + this.amount + DESCRIPTIONS[1] + this.rarityDesc() + DESCRIPTIONS[2];
	}
	
	private static boolean checkValid(AbstractCard c) {
		return !(c instanceof AbstractTestCurseCard || c instanceof AbstractMahjongCard);
	}
	
	private ArrayList<AbstractCard> getList(CardRarity rarity, CardColor color) {
		ArrayList<AbstractCard> list = new ArrayList<AbstractCard>();
		for (Map.Entry<String, AbstractCard> c : CardLibrary.cards.entrySet()) {
			AbstractCard card = (AbstractCard) c.getValue();
			if ((card.color == color) && (card.rarity == rarity) && checkValid(card)) {
				list.add(card);
			}
		}
		if (list.isEmpty()) {
			for (AbstractCard c : BaseMod.getCustomCardsToAdd()) {
				if (c.color == color && fuckedRarity) {
					AbstractCard tmp = c.makeCopy();
					try {
						c = tmp;
						c.getClass().getMethod("initInfo").invoke(tmp);
						if (c.getClass().getField("realRarity").get(c) == rarity) {
							list.add(c);
							continue;
						}
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
							| NoSuchMethodException | SecurityException | NoSuchFieldException e) {
						e.printStackTrace();
					}
				} else if (c.color == color && c.rarity == rarity && checkValid(c)) {
					list.add(c);
				}
			}
		}
		TestMod.info("临时删除power: 数组大小 = " + list.size());
		return list;
	}
	
	private static CardGroup getGroup(ArrayList<AbstractCard> list) {
		CardGroup group = new CardGroup(CardGroupType.UNSPECIFIED);
		group.group = list;
		return group;
	}
	
	public void atStartOfTurn() {
		if (!AbstractDungeon.getMonsters().areMonstersBasicallyDead()) {
			flash();
			for (int i = 0; i < this.amount; i++) {
				this.addToBot(new MakeTempCardInHandAction(group.getRandomCard(AbstractDungeon.cardRandomRng).makeCopy(), 1, false));
			}
		}
	}
    
}
