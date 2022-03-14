package testmod.events;

public class 示例事件 extends AbstractTestEvent {

	@Override
	protected void intro() {
		this.imageEventText.updateBodyText(desc()[1]);
		this.imageEventText.updateDialogOption(0, option()[1] + option()[2], null);
		this.imageEventText.setDialogOption(option()[3]);
	}

	@Override
	protected void choose(int choice) {
		switch (choice) {
		case 0:
			logMetric("Took Card");
			break;
		default:
			logMetric("Ignored");
		}
		this.imageEventText.updateBodyText(desc()[3]);
		this.imageEventText.updateDialogOption(0, option()[4]);
		this.imageEventText.clearRemainingOptions();
	}
	
}