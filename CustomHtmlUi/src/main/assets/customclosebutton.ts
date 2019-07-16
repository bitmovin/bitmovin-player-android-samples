import { ToggleButton, ToggleButtonConfig } from './togglebutton';
import { PlayerAPI } from 'bitmovin-player';
import { UIInstanceManager } from '../uimanager';

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
      cssClass: 'ui-customclosetogglebutton',
      text: 'close',
    };

    this.config = this.mergeConfig(config, defaultConfig, this.config);
  }

  configure(player: PlayerAPI, uimanager: UIInstanceManager): void {
    super.configure(player, uimanager);

    if (window.bitmovin.customMessageHandler) {
      window.bitmovin.customMessageHandler.on('toggleCloseButton', (data?: string) => {
        if (this.isEnabled()) {
          this.disable();
        } else {
          this.enable();
        }
      });

      this.onClick.subscribe(() => {
        let result = window.bitmovin.customMessageHandler.sendSynchronous('closePlayer');
        console.log('Return value from native:', result);
        window.bitmovin.customMessageHandler.sendAsynchronous('closePlayerAsync');
      });
    }
  }
}
