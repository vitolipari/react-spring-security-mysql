import React from "react";
import { BrowserRouter, Routes, Route } from "react-router-dom";
import logo from './logo.svg';
import './App.css';


function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={ props => (<div>Home</div>)}>
          {/*<Route index element={<Home />} />          */}

          <Route 
            path="/sign" 
            element={ props => {
              console.log("rotta prova");
              console.log( props );
              return (
                <React.Fragment>
                  <div>SignIn / SignUp</div>
                  <code>props</code><br/>
                  <pre>
                    {
                      JSON.stringify(props, null, 2)
                    }
                  </pre>
                </React.Fragment>                
              )
            }} 
          />
          <Route 
            path="/sign-in" 
            element={ props => (<div>SignIn</div>) } 
          />
          <Route 
            path="/sign-up" 
            element={ props => (<div>SignUp</div>) } 
          />

          <Route 
            path="/prova" 
            element={
              props => {
                console.log("path prova");
                console.log( props );
                return (
                  <div>Prova rotta publica</div>
                );
              }
            } 
          />
          <Route 
            path="*" 
            element={ props => (<div>Pagine non trovata</div>) } 
          />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;
