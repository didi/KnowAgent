import * as React from 'react';
import * as monaco from 'monaco-editor';
import format2json from 'format-to-json';
import { Input } from 'antd';
import './index.less';

export interface IEditorProps {
  style?: React.CSSProperties;
  options: monaco.editor.IStandaloneEditorConstructionOptions;
  uri?: monaco.Uri;
  autoUnmount?: boolean;
  customMount?: (editor: monaco.editor.IStandaloneCodeEditor, monaco: any) => any;
  placeholder?: string;
  value: string;
  onChange?: any;
}

class Monacoeditor extends React.Component<IEditorProps | any> {
  public ref: HTMLElement | any;
  public editor: monaco.editor.IStandaloneCodeEditor | any;
  public state = {
    placeholder: '',
    flag: false
  };

  public async componentDidMount() {
    const { value, onChange } = this.props;
    const format: any = await format2json(value);
    this.editor = monaco.editor.create(this.ref, {
      value: format.result || value,
      language: 'json',
      lineNumbers: 'off',
      scrollBeyondLastLine: false,
      automaticLayout: true, // 自动适应布局
      minimap: {
        enabled: false,
      },
      glyphMargin: true, // 字形边缘 {},[]
    });
    this.editor.onDidChangeModelContent(() => {
      const newValue = this.editor.getValue();
      onChange(newValue);
    });
  }
  public componentDidUpdate() {
    const { value } = this.props;
    if (!this.state.flag && value) {
      this.editor.setValue(value);
      this.setState({ flag: true });
    }
  }

  public componentWillUnmount() {
    this.editor.dispose()
  }
  public render() {
    return (
      // monacoEditor 最小高为85px 如需自定义，请在props中传入 height
      <div className="monacoEditor ant-input" style={{ height: `${this.props.height ? this.props.height : ''}px` }} >
        {/* <Input style={{ display: 'none' }} {...this.props} /> */}
        <div className="editor" {...this.props} ref={(id) => { this.ref = id; }} />
      </div>
    );
  }
}
export default Monacoeditor;
