package testmod.relics;

public class AaSapphireKey extends AbstractTestRelic {
	public static final AaSapphireKey PRE = new AaSapphireKey();
	public static final AaSapphireKey KEY = new AaSapphireKey();
	
	public AaSapphireKey() {
		super(RelicTier.DEPRECATED, LandingSound.CLINK);
	}
}