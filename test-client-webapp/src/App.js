import React from 'react';
import logo from './logo.svg';
import './App.css';
import { Switch, Route } from 'react-router-dom';
import HomePage from './Home/HomePage';
import Header from './Components/Header/Header';
import NotFound from './Errors/NotFound';
import SSOPage from './SSO/SSOPage';

function App() {
  return (
    <div className="App">
      <Header />
      <div className="core row offset-md-3 col-md-6">
        <Switch>
          <Route exact path="/" component={HomePage} /> 
          <Route exact path="/sso" component={SSOPage} />
          <Route component={NotFound} />
        </Switch>
      </div>
    </div>
  );
}

export default App;
