import React from 'react';
import './App.scss';
import HeadBar from "./components/HeadBar";
import Editor from "./components/Editor";
import {convertTOBackend, convertToFrontend, logInfo, processError} from "./services/utils";
import {EC_SITES, SCRAPING_TYPE} from "./config/config";
import _ from 'lodash';
import Setting from "./components/Setting";
import 'sweetalert2/src/sweetalert2.scss';
import 'nprogress/nprogress.css';
import Swal from 'sweetalert2'
import Api from "./services/api";
import {getI18T} from "./i18nSetup";
import Button from "./components/Button";

class App extends React.Component {
  constructor(props, context) {
    super(props, context);

    this.state = {
      site: EC_SITES[0],
      type: SCRAPING_TYPE[0],
      siteObj: null,
      logTxt: [],
      log: false,
      setting: false,
      loadType: 'pending'
    }
    this.onHeaderDropDownChange = this.onHeaderDropDownChange.bind(this);
    this.onUpdate = this.onUpdate.bind(this);
    this.t = getI18T()
  }

  /**
   * header dropdown change
   * @param key the key
   * @param option the option
   */
  onHeaderDropDownChange(key, option) {
    this.setState({...this.state, [key]: option})
  }

  /**
   * load site
   */
  async loadSite() {
    const {siteObj, site, type} = this.state;
    if (siteObj) {
      const result = await Swal.fire({
        title: this.t('loadDialogTitle'),
        text: this.t('loadDialogContent'),
        showCancelButton: true,
        showConfirmButton: true,
        confirmButtonText: this.t('dialogBtnYes'),
        cancelButtonText: this.t('dialogBtnNo')
      });
      if (result.dismiss) {
        return;
      }
    }
    this.setState({loadType: 'loading'})
    try {
      const rsp = await Api.load(site.value, type.value)
      this.setState({
        siteObj: convertToFrontend(rsp),
        loadType: 'loaded',
      })
      logInfo('json loaded.')
    } catch (e) {
      this.setState({siteObj: null, loadType: 'loaded'})
      processError(e)
    }
  }

  async componentDidMount() {
    window.log = (text) => this.setState({logTxt: [`[${new Date().toISOString()}]: ${text}`].concat(this.state.logTxt)});
    window.log('extension load succeed')
  }

  componentWillUnmount() {
    window.log = _.noop;
  }


  /**
   * update json value
   * @param path the path
   * @param value the value
   */
  onUpdate(path, value) {
    const siteObj = _.cloneDeep(this.state.siteObj)
    if (value === null && path.indexOf('rows') >= 0) {
      const parts = path.split('.');
      const index = parseInt(parts.pop())
      _.get(siteObj, parts.join('.')).splice(index, 1)
    } else {
      _.set(siteObj, path, value)
    }
    this.setState({siteObj})
  }

  /**
   * test site
   */
  async testSite() {
    const {siteObj, site, type} = this.state;

    const result = await Swal.fire({
      title: this.t('testDialogTitle'),
      showCancelButton: true,
      showConfirmButton: true,
      confirmButtonText: this.t('dialogBtnYes'),
      cancelButtonText: this.t('dialogBtnNo')
    });
    if (result.dismiss) {
      return;
    }

    try {
      const rsp = await Api.test(site.value, type.value, convertTOBackend(siteObj))
      logInfo('test succeed')
      logInfo(rsp)
    } catch (e) {
      processError(e)
    }
  }

  /**
   * save site json
   * @return {Promise<void>}
   */
  async saveSite() {
    const {siteObj, site, type} = this.state;

    const result = await Swal.fire({
      title: this.t('saveDialogTitle'),
      showCancelButton: true,
      showConfirmButton: true,
      confirmButtonText: this.t('dialogBtnYes'),
      cancelButtonText: this.t('dialogBtnNo')
    });
    if (result.dismiss) {
      return;
    }

    try {
      const body = convertTOBackend(siteObj);
      logInfo(JSON.stringify(body))
      await Api.save(site.value, type.value, body)
      logInfo('json saved')
    } catch (e) {
      processError(e)
    }
  }

  /**
   * on log panel change
   */
  onLog() {
    this.setState({log: !this.state.log})
  }

  render() {
    const {setting, log, logTxt} = this.state;

    if (setting) {
      return <div className='app'>
        <Setting onBack={() => this.setState({setting: false})}/>
      </div>
    }
    return (
      <div className="app">
        <HeadBar onChange={this.onHeaderDropDownChange}
                 onLoad={() => this.loadSite()}
                 onTest={() => this.testSite()}
                 onSave={() => this.saveSite()}
                 onLog={() => this.onLog()}
                 onSetting={() => this.setState({setting: true})}
                 {...this.state}/>
        <Editor ref={ref => this.editor = ref} {...this.state} onUpdate={this.onUpdate}/>
        {log && <div className='log-container'>
          <div className='log-container'>
            {_.map(logTxt, (text, i) => (<div key={`log-${i}`}>{text}</div>))}
          </div>
          <Button title={"Close Log"} onClick={()=>this.onLog()}/>
        </div>}
      </div>
    );
  }
}

export default App;
