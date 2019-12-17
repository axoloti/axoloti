#ifndef USB_PATCH_H
#define USB_PATCH_H

typedef void (usbh_hid_custom_report_callback_t)(uint8_t *hid_report, int len);
extern void register_usbh_hid_custom_report_cb(usbh_hid_custom_report_callback_t *cb);
extern void unregister_usbh_hid_custom_report_cb(usbh_hid_custom_report_callback_t *cb);

extern int8_t hid_buttons[8];
extern int8_t hid_mouse_x;
extern int8_t hid_mouse_y;

#endif
