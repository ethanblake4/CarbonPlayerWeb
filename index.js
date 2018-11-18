/** @format */

import {AppRegistry} from 'react-native';
import {name as appName} from './app.json';
import Boot from './out/build/reactnative/src/Boot';

AppRegistry.registerComponent(appName, () => Boot);