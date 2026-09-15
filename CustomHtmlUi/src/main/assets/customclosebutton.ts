import { ToggleButton, ToggleButtonConfig } from './ToggleButton';
import { PlayerAPI } from 'bitmovin-player';
import { UIInstanceManager } from '../../UIManager';

declare const window: any;

interface CallbackInterface {
  (data?: string): void;
}

interface CustomMessageHandlerApi {
  sendSynchronous(name: string, data?: string): string | null;
  sendAsynchronous(name: string, data?: string): void;
  on(methodName: string, callback: CallbackInterface): void;
}

export class CustomCloseButton extends ToggleButton<ToggleButtonConfig> {
  constructor(config: ToggleButtonConfig = {}) {
    super(config);

    const defaultConfig: ToggleButtonConfig = {
      cssClass: 'ui-custom-close-toggle-button',
      text: 'close',
    };

    this.config = this.mergeConfig(config, defaultConfig, this.config);
  }

  configure(player: PlayerAPI, uimanager: UIInstanceManager): void {
    super.configure(player, uimanager);

    if (window.bitmovin.customMessageHandler) {
      // eslint-disable-next-line @typescript-eslint/no-unsafe-call
      window.bitmovin.customMessageHandler.on('toggleCloseButton', (data?: string) => {
        if (this.isEnabled()) {
          this.disable();
        } else {
          this.enable();
        }
      });

      this.onClick.subscribe(() => {
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        const result = window.bitmovin.customMessageHandler.sendSynchronous('closePlayer');
        console.log('Return value from native:', result);
        // eslint-disable-next-line @typescript-eslint/no-unsafe-call
        window.bitmovin.customMessageHandler.sendAsynchronous('closePlayerAsync');
      });
    }
  }
}
