import { ReactNode, Component } from 'react';
import { TextStyle, ViewStyle } from 'react-native';

export interface Highlight {
  id?: string;
  start: number;
  end: number;
}

export interface SelectionEvent {
  eventType: string;
  content: string;
  selectionStart: number;
  selectionEnd: number;
}

export interface SelectableTextProps {
  value?: string;
  onSelection?: (event: SelectionEvent) => void;
  menuItems?: string[];
  style?: TextStyle | ViewStyle;
  highlights?: Highlight[];
  highlightColor?: string;
  onHighlightPress?: (id?: string) => void;
  appendToChildren?: ReactNode;
  TextComponent?: typeof Component | React.ComponentType;
  textValueProp?: string;
  textComponentProps?: Record;
  children?: ReactNode;
}