import { css } from '@emotion/react';
import React from 'react';
import { useQueryClient } from 'react-query';
import { Link, useNavigate } from 'react-router-dom';
/** @jsxImportSource @emotion/react */

const layout = css`
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 100px 300px;
`; 

const header = css`
  display: flex ;
  justify-content: center;
  margin-bottom: 40px;

  & > input {
    box-sizing: border-box;
    width: 50%;
    height: 50px;
    border-radius: 50px;
    padding: 10px 20px;
  }
`;

const main = css`
    display: flex;
    justify-content: space-between;
`;

const leftBox = css`
    box-sizing: border-box;
    border: 2px solid #dbdbdb;
    border-radius: 10px;
    width: 64%;
`;

const rightBox = css`
    box-sizing: border-box;
    display: flex;
    flex-direction: column;
    align-items: center;
    border: 2px solid #dbdbdb;
    border-radius: 10px;
    width: 35%;
    padding: 20px;

    & > button {
        margin-bottom: 10px;
        width: 100%;
        height: 30px;
        font-size: 16px;
        font-weight: 600;

        & > a {
            width: 100%;

        }
    }

    & > div {
        display: flex;
        justify-content: center;
        width: 100%;

        & > a:not(:nth-last-of-type(1))::after { // a 태그들 중 마지막의 첫번째가 아니면 after에 아래의 css를 적용한다. 
            display: inline-block;
            content: "";
            margin: 0px 5px;
            height: 60%;
            border-left: 1px solid #222222;
        }
    }
`;

const userInfoBox = css`
    display: flex;  
    justify-content: flex-start;
    width: 100%;

    `;

const profileImgBox = css`
    box-sizing: border-box;
    display: flex;
    justify-content: center;
    align-items: center;
    border-radius: 50%;
    width: 64px;
    height: 64px;
    box-shadow: 0px 0px 2px #00000088;

`;

const profileInfo = css`
    box-sizing: border-box;
    display: flex;
    justify-content: space-between;
    flex-grow: 1;
    padding: 10px;

    & > button {
        box-sizing: border-box;
        border: 1px solid #dbdbdb;
        border-radius: 37px;
        padding: 5px 10px;
        height: 37px;
        background-color: #ffffff;
        color: #555555;
        font-size: 16px;
        cursor: pointer;

    }

`;

function IndexPage(props) {
    const navigate = useNavigate(); // useNavigate를 사용하면 페이지 이동이 가능하다.

    const queryClient = useQueryClient();
    //const data = queryClient.getQueryData("userInfoQuery"); // 쿼리의 데이터를 가져온다.
    const userInfoState = queryClient.getQueryState("userInfoQuery"); // 쿼리의 상태를 가져온다.
    const accessTokenValidState = queryClient.getQueryState("accessTokenValidQuery"); // 쿼리의 상태를 가져온다.
    // console.log(data);
    // console.log(state);

    // queryClient.invalidateQueries(); // 지금까지 들고온 쿼리를 만료시킨다. 만료되면 다시 실행

    const handleLoginButtonOnClick = () => {
        navigate("/user/login");    // 페이지 이동
    }

    const handleLogoutButtonOnClick = () => {
        localStorage.removeItem("accessToken");
        window.location.replace("/");
    }

    return (
        <div css={layout}>
            <header css={header}>
                <input type="search" placeholder='검색어를 입력해 주세요.' />
            </header>
            {
                accessTokenValidState.status === "idle" || accessTokenValidState.status === "loading"
                ? <h1>로딩중....</h1>
                :
                <main css={main}>
                    <div css={leftBox}></div>
                    {
                        userInfoState.status === "success" &&
                        <div css={rightBox}>
                            <div css={userInfoBox}>
                                <div css={profileImgBox}>
                                    <img src="" alt="" />
                                </div>
                                <div css={profileInfo}>
                                    <div>
                                        <div>{userInfoState.data.data.username}님</div>
                                        <div>{userInfoState.data.data.email}</div>
                                    </div>
                                    <button onClick={handleLogoutButtonOnClick}>로그아웃</button>
                                </div>
                            </div>
                        </div>
                    }  
                    {
                        userInfoState.status !== "success" &&
                        <div css={rightBox}>
                            <p>더 안전하고 편리하게 이용하세요</p>
                            <button onClick={handleLoginButtonOnClick}>로그인</button>  
                            <div>
                                <Link to={"/user/help/id"}>아이디 찾기</Link>  
                                <Link to={"/user/help/pw"}>비밀번호 찾기</Link> 
                                <Link to={"/user/join"}>회원가입</Link>
                            </div>
                        </div>
                    }
                </main>
            }
        </div>
    );
}

export default IndexPage;