import React from "react";
import {
  createBrowserRouter,
  RouterProvider,
  Route,
  Link,
} from "react-router-dom";
import logo from './logo.svg';
import './App.css';
import "./style/index.css";
import {SignupPage, SigninPage} from "./pages";


const Home = props => {
    console.log("rotta Home");
    console.log( props );
    return (
        <div className="App">
          <header className="App-header">
            <img src={logo} className="App-logo" alt="logo" />
            <p>
              Public Free Home
            </p>
            <Link to={"/public/sign-in"}>SignIn</Link>
            <Link to={"/public/sign-up"}>SignUp</Link>
          </header>
        </div>
    )
};

const Prova = props => {
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
}

const SignOut = props => (<div>SignOut</div>);

const NotFound = props => (<div>Not Found</div>);



const allPublicFreeRoutes = createBrowserRouter([
    {
        exact: true,
        path: "/",
        element: <Home/>
    },
    {
        exact: true,
        path: "/public/sign-in",
        element: <SigninPage/>
    },
    {
        exact: true,
        path: "/public/sign-up",
        element: <SignupPage/>
    },
    {
        exact: true,
        path: "/public/test",
        element: (
            <Prova
                data={{ id: 1, name: "datas" }}
            />
        )
    },
    {
        exact: true,
        path: "*",
        element: <NotFound/>
    }
]);


function App() {
  return (
    <RouterProvider
        router={ allPublicFreeRoutes }
    />
  );
}

export default App;
