import React, { Component } from 'react';
import {
  requireNativeComponent,
  UIManager,
  Platform,
  findNodeHandle,
  Text,
  View,
  NativeSyntheticEvent,
} from 'react-native';
import type { SelectableTextProps, SelectionEvent, Highlight } from './types';

const LINKING_ERROR =
  `The package 'react-native-selectable-text-modern' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const ComponentName = 'RNSelectableText';

const RNSelectableTextView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<any>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };

interface NativeSelectionEvent {
  nativeEvent: SelectionEvent;
}

interface NativeHighlightEvent {
  nativeEvent: {
    id?: string;
  };
}

export class SelectableText extends Component<SelectableTextProps> {
  static defaultProps = {
    value: '',
    menuItems: [],
    highlights: [],
    highlightColor: '#FFFF00',
    textValueProp: 'children',
    onSelection: () => {},
    onHighlightPress: () => {},
    textComponentProps: {},
  };

  private _root: any;

  componentDidMount() {
    if (Platform.OS === 'android') {
      this._setupAndroid();
    }
  }

  componentDidUpdate(prevProps: SelectableTextProps) {
    if (
      Platform.OS === 'android' &&
      (prevProps.menuItems !== this.props.menuItems ||
        prevProps.value !== this.props.value)
    ) {
      this._setupAndroid();
    }
  }

  _setupAndroid = () => {
    const node = findNodeHandle(this._root);
    if (node && UIManager.dispatchViewManagerCommand) {
      UIManager.dispatchViewManagerCommand(
        node,
        UIManager.getViewManagerConfig(ComponentName).Commands.setupMenuItems.toString(),
        [this.props.menuItems || []]
      );
    }
  };

  _onSelection = (event: NativeSyntheticEvent<NativeSelectionEvent>) => {
    if (this.props.onSelection) {
      this.props.onSelection(event.nativeEvent);
    }
  };

  _onHighlightPress = (event: NativeSyntheticEvent<NativeHighlightEvent>) => {
    if (this.props.onHighlightPress) {
      this.props.onHighlightPress(event.nativeEvent.id);
    }
  };

  render() {
    const {
      value,
      style,
      highlights,
      highlightColor,
      appendToChildren,
      TextComponent,
      textValueProp,
      textComponentProps,
      children,
      menuItems,
      ...rest
    } = this.props;

    const textValue = value || children;
    const Component = TextComponent || Text;
    const textProps = {
      ...textComponentProps,
      [textValueProp || 'children']: textValue,
    };

    return (
      <RNSelectableTextView
        ref={(ref: any) => (this._root = ref)}
        {...rest}
        style={style}
        value={String(textValue || '')}
        menuItems={menuItems}
        highlights={highlights}
        highlightColor={highlightColor}
        onSelection={this._onSelection}
        onHighlightPress={this._onHighlightPress}
      >
        <Component {...textProps} />
        {appendToChildren && <View>{appendToChildren}</View>}
      </RNSelectableTextView>
    );
  }
}

// Export types using 'export type' syntax
export type { SelectableTextProps, SelectionEvent, Highlight } from './types';

// Export the component as default
export default SelectableText;
