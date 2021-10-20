package cards;

import java.util.HashMap;

import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.localization.CardStrings;
import basemod.ReflectionHacks;
import basemod.abstracts.CustomCard;
import mymod.TestMod;
import utils.MiscMethods;

public abstract class AbstractTestCard extends CustomCard implements MiscMethods {
	
	/**
	 * 无色牌用
	 * @param shortID
	 * @param NAME
	 * @param IMG
	 * @param COST
	 * @param DESCRIPTION
	 * @param type
	 * @param rarity
	 * @param target
	 */
	public AbstractTestCard(String shortID, String NAME, int COST, String DESCRIPTION, CardType type,
			CardRarity rarity, CardTarget target) {
		super(TestMod.makeID(shortID), NAME, TestMod.cardIMGPath(shortID), COST, DESCRIPTION, type,
				CardColor.COLORLESS, rarity, target);
	}

	/**
	 * 诅咒用
	 * @param shortID
	 * @param NAME
	 * @param IMG
	 * @param COST
	 * @param DESCRIPTION
	 */
	public AbstractTestCard(String shortID, String NAME, String DESCRIPTION) {
		super(TestMod.makeID(shortID), NAME, TestMod.cardIMGPath(shortID), 1, DESCRIPTION, CardType.CURSE,
				CardColor.CURSE, CardRarity.SPECIAL, CardTarget.NONE);
	}
	
	private AbstractTestCard(String shortID, int COST, CardType type, CardRarity rarity, CardTarget target) {
		this(shortID, Strings(shortID).NAME, COST, Strings(shortID).DESCRIPTION, type, rarity, target);
	}
	
	public <T extends AbstractTestCard> AbstractTestCard(Class<T> c, int cost, CardType type, CardRarity rarity,
			CardTarget target) {
		this(shortID(c), cost, type, rarity, target);
	}
	
	protected static CardStrings Strings(String ID) {
		if (!CS.containsKey(ID))
			CS.put(ID, CardCrawlGame.languagePack.getCardStrings(TestMod.makeID(ID)));
		return CS.get(ID);
	}
	
	protected static String name(String id) {
		return Strings(id).NAME;
	}
	
	protected static String desc(String id) {
		String tmp = Strings(id).DESCRIPTION;
		return tmp == null ? "" : tmp;
	}
	
	protected static String desc1(String id) {
		return Strings(id).UPGRADE_DESCRIPTION;
	}
	
	protected static String[] extendDesc(String id) {
		return Strings(id).EXTENDED_DESCRIPTION;
	}
	
	protected String[] exDesc() {
		return extendDesc(shortID());
	}
	
	protected String desc() {
		return desc(shortID());
	}
	
	protected String upgradedDesc() {
		return desc1(shortID());
	}
	
	public void upDesc(String desc) {
		this.rawDescription = desc;
		this.initializeDescription();
	}
	
	public void upDesc() {
		this.rawDescription = desc1(shortID());
		this.initializeDescription();
	}
	
	protected String name() {
		return name(shortID());
	}
	
	protected String shortID() {
		return shortID(this.getClass());
	}
	
	protected static <T extends AbstractTestCard> String shortID(Class<T> c) {
		if (!IDS.containsKey(c)) {
			String tmp = ReflectionHacks.getPrivateStatic(c, "ID");
			IDS.put(c, tmp == null ? c.getSimpleName() : tmp);
		}
		return IDS.get(c);
	}

	private static final HashMap<String, CardStrings> CS = new HashMap<String, CardStrings>();
	public static final HashMap<Class<? extends AbstractTestCard>, String> IDS = 
			new HashMap<Class<? extends AbstractTestCard>, String>();

}
